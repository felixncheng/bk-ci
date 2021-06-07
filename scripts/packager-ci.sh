#!/bin/bash
# 生成bk-ci安装包.
# 收集编译产物, 生成所需的安装包目录, 然后打包.
set -eu
trap "on_ERR;" ERR
on_ERR (){
  local fn=$0 ret=$? lineno=${BASH_LINENO:-$LINENO}
  echo >&2 "ERROR $fn exit with $ret at line $lineno: $(sed -n ${lineno}p $0)."
}

my_path="$(readlink -f "$0")"
my_dir=${my_path%/*}
[ -d "$my_dir" ] || { echo >&2 "ERROR: my_dir is NOT an existed dir: $my_dir."; return 3; }
cmd_collect_ci_ms_name="$my_dir/bk-ci-collect-ms-name.sh"
cmd_ci_slim="$my_dir/bk-ci-slim.sh"

collect_frontend (){
  mkdir -p "$ci_pkg_dir/frontend" "$ci_pkg_dir/support-files/templates"
  cp -a "$ci_bin_frontend_dir/." "$ci_pkg_dir/frontend"
  echo "collect page templates."
  find "$ci_pkg_dir/frontend" -name "frontend#*" -exec mv -v {} "$ci_pkg_dir/support-files/templates" \;
}

collect_goagent (){
  mkdir -p "$1"
  cp -r "$ci_bin_goagent_dir/"* "$1"
}
collect_workeragent (){
  mkdir -p "$1"
  cp -r "$ci_bin_agentjar_dir/"worker-agent.jar "$1"
}
collect_agentpackage (){
  cp -r "$ci_code_dir/support-files/agent-package" "$ci_pkg_dir"
  collect_workeragent "$ci_pkg_dir/agent-package"
  collect_goagent "$ci_pkg_dir/agent-package/jar"
}

collect_backend (){
  # 收集fatjar, slim化.
  svcs=$(ls "$ci_bin_msjar_dir" | sed -n 's/boot-\(.*\).jar/\1/p')
  for ms in $svcs; do
    "$cmd_ci_slim" "$ms" "$ci_bin_msjar_dir/boot-$ms.jar" "$ci_pkg_dir"
  done
  # 清理ci_ms_wip
  ci_template_dir="$ci_pkg_dir/support-files/templates"
  shopt -s nullglob
  echo "remove ms wip: $ci_ms_wip."
  for ms in ${ci_ms_wip//,/ }; do
    if [ -d "${ci_pkg_dir:-ci_pkg_dir}/${ms:-ms}/" ]; then
      rm -rvf "${ci_pkg_dir:-ci_pkg_dir}/${ms:-ms}/"
    fi
    for f in "$ci_template_dir/$ms#"* "$ci_template_dir/"*"#$ms#"*; do
      rm -vf "$ci_template_dir/$f"
    done
  done
  shopt -u nullglob
}

collect_support_files (){
  cp -r "$ci_code_dir/support-files/" "$ci_pkg_dir"
  rm -rf "$ci_pkg_dir"/support-files/agent-package
}

collect_scripts (){
  cp -r "$ci_code_dir/scripts" "$ci_pkg_dir"
}

prepare_agentless (){
  echo "generate agentless."
  cp -r "$ci_pkg_dir/dockerhost" "$ci_pkg_dir/agentless"
}

packager_ci (){
  mkdir -p "$ci_pkg_dir"
  echo "ci_code_dir is $ci_code_dir."
  echo "ci_pkg_dir is $ci_pkg_dir."
  echo "collect_support_files"
  collect_support_files
  echo "collect_agentpackage"
  collect_agentpackage
  echo "collect_scripts"
  collect_scripts
  echo "collect_backend"
  collect_backend
  echo "collect_frontend"
  collect_frontend
  echo "gen version:"
  echo "$VERSION" | tee "$ci_pkg_dir/VERSION"
  echo "BK_CI_VERSION=\"$VERSION\"" | tee -a "$ci_pkg_dir/scripts/bkenv.properties"
  (cd "$ci_pkg_dir/.."; tar acf "$ci_pkg_path" "$(basename "$ci_pkg_dir")"; )
  ls -l "$ci_pkg_path"
}

if [ $# -lt 2 ]; then
  echo "Usage: $0 VERSION CI_PKG_PATH CI_CODE_DIR  -- make install package"
  echo " VERSION      version string. example: v1.X.X-desc"
  echo " CI_PKG_PATH  generated install package path."
  echo " CI_CODE_DIR  source code with compiled binaries."
  echo "Example: $0 v1.5.0-RELEASE workspace/bkci-slim.tar.gz workspace/bk-ci-v1.5.0"
  echo "ENV:"
  echo " ci_pkg_dir   temp dir contains ci package files, should end with /ci/."
  echo " ci_ms_wip    ms still wip, should be removed if exist. comma separated."
  exit 0
else
  VERSION=$1
  ci_pkg_path=$2
  ci_code_dir="${3:-${my_dir%/*}}"  # 默认为本脚本的上层目录.
  ci_pkg_dir="${ci_pkg_dir:-$ci_code_dir/ci}"  # 默认为代码目录下的ci目录.
  ci_ms_wip="${ci_ms_wip:-sign}"  # sign需要重构优化, 暂不能用.
  # 编译后的目录
  ci_bin_frontend_dir="${ci_bin_frontend_dir:-$ci_code_dir/src/frontend/frontend/}"
  ci_bin_goagent_dir=${ci_bin_goagent_dir:-$ci_code_dir/src/agent/bin/}
  ci_bin_agentjar_dir=${ci_bin_agentjar_dir:-$ci_code_dir/src/backend/ci/release/}
  ci_bin_msjar_dir=${ci_bin_msjar_dir:-$ci_code_dir/src/backend/ci/release/}

  packager_ci
fi
