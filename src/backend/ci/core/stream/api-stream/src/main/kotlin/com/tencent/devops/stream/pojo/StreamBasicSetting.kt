/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.stream.pojo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("蓝盾stream 项目配置V2")
data class StreamBasicSetting(
    @ApiModelProperty("stream 项目ID")
    override val gitProjectId: Long,
    @ApiModelProperty("stream 项目名")
    override val name: String,
    @ApiModelProperty("stream 项目url")
    override val url: String,
    @ApiModelProperty("homepage")
    override val homepage: String,
    @ApiModelProperty("gitHttpUrl")
    override val gitHttpUrl: String,
    @ApiModelProperty("gitSshUrl")
    override val gitSshUrl: String,
    @ApiModelProperty("是否启用CI")
    val enableCi: Boolean,
    @ApiModelProperty("Build pushed branches")
    val buildPushedBranches: Boolean = true,
    @ApiModelProperty("Build pushed pull request")
    val buildPushedPullRequest: Boolean = true,
    @ApiModelProperty("创建时间")
    val createTime: Long?,
    @ApiModelProperty("修改时间")
    val updateTime: Long?,
    @ApiModelProperty("蓝盾项目Code")
    val projectCode: String?,
    @ApiModelProperty("是否开启Mr锁定")
    val enableMrBlock: Boolean = true,
    @ApiModelProperty("Stream开启人")
    val enableUserId: String,
    @ApiModelProperty("Stream开启人所在事业群")
    var creatorBgName: String?,
    @ApiModelProperty("Stream开启人所在部门")
    var creatorDeptName: String?,
    @ApiModelProperty("Stream开启人所在中心")
    var creatorCenterName: String?,
    @ApiModelProperty("GIT项目的描述信息")
    val gitProjectDesc: String?,
    @ApiModelProperty("GIT项目的头像信息")
    val gitProjectAvatar: String?,
    @ApiModelProperty("带有名空间的项目名称")
    val nameWithNamespace: String,
    @ApiModelProperty("带有名空间的项目路径")
    val pathWithNamespace: String?,
    @ApiModelProperty("项目最后一次构建的CI信息")
    val lastCiInfo: StreamCIInfo?,
    @ApiModelProperty("项目下构建是否发送commitCheck")
    val enableCommitCheck: Boolean = true,
    @ApiModelProperty("项目下构建是否发送mrComment")
    val enableMrComment: Boolean = true,
    @ApiModelProperty("pr、mr触发时的权限校验")
    val triggerReviewSetting: TriggerReviewSetting = TriggerReviewSetting()
) : StreamBaseRepository(gitProjectId, name, url, homepage, gitHttpUrl, gitSshUrl)

@ApiModel("蓝盾stream 页面修改配置")
data class StreamUpdateSetting(
    @ApiModelProperty("Build pushed branches")
    val buildPushedBranches: Boolean,
    @ApiModelProperty("Build pushed pull request")
    val buildPushedPullRequest: Boolean,
    @ApiModelProperty("是否开启Mr锁定")
    val enableMrBlock: Boolean
)

@ApiModel("mr触发时的权限校验相关配置")
data class TriggerReviewSetting(
    @ApiModelProperty("主库开发者及以上的用户提交的pr、mr是否默认给触发")
    val memberNoNeedApproving: Boolean = true,
    @ApiModelProperty("白名单，可以是用户或者项目id")
    val whitelist: List<String> = emptyList()
)
