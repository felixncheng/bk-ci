package com.tencent.bk.codecc.defect.service.specialparam;

import com.google.common.collect.Maps;
import com.tencent.bk.codecc.defect.model.CheckerDetailEntity;
import com.tencent.devops.common.constant.ComConstants;
import com.tencent.devops.common.util.ObjectDynamicCreator;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PYLINT特殊参数处理
 *
 * @version V4.0
 * @date 2019/8/15
 */
@Service("PYLINTSpecialParamBizService")
public class PYLINTSpecialParamServiceImpl extends AbstractSpecialParamServiceImpl
{
    /**
     * 特殊参数是否相同
     *
     * @param toolName
     * @param paramJson1
     * @param paramJson2
     */
    @Override
    public boolean isSameParam(String toolName, String paramJson1, String paramJson2)
    {
        JSONObject paramJsonObj1 = new JSONObject(paramJson1);
        JSONObject paramJsonObj2 = new JSONObject(paramJson2);
        return isSameParam(paramJsonObj1, paramJsonObj2, ComConstants.PARAM_PYLINT_PY_VERSION);
    }
}
