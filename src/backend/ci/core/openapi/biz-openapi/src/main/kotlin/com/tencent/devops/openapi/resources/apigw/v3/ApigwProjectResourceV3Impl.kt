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
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tencent.devops.openapi.resources.apigw.v3

import com.tencent.devops.common.client.Client
import com.tencent.devops.common.web.RestResource
import com.tencent.devops.openapi.api.apigw.v3.ApigwProjectResourceV3
import com.tencent.devops.project.api.service.ServiceProjectResource
import com.tencent.devops.project.pojo.ProjectCreateInfo
import com.tencent.devops.project.pojo.ProjectUpdateInfo
import com.tencent.devops.project.pojo.ProjectVO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@RestResource
class ApigwProjectResourceV3Impl @Autowired constructor(private val client: Client) : ApigwProjectResourceV3 {
    companion object {
        private val logger = LoggerFactory.getLogger(ApigwProjectResourceV3Impl::class.java)
    }

    override fun create(
        appCode: String?,
        apigwType: String?,
        userId: String,
        projectCreateInfo: ProjectCreateInfo,
        accessToken: String?
    ): com.tencent.devops.project.pojo.Result<Boolean> {
        logger.info("create project projectCreateInfo($projectCreateInfo) by user $userId")
        return client.get(ServiceProjectResource::class).create(
            userId = userId,
            projectCreateInfo = projectCreateInfo
        )
    }

    override fun update(
        appCode: String?,
        apigwType: String?,
        userId: String,
        projectId: String,
        projectUpdateInfo: ProjectUpdateInfo,
        accessToken: String?
    ): com.tencent.devops.project.pojo.Result<Boolean> {
        logger.info("update project projectId($projectId) projectCreateInfo($projectUpdateInfo) by user $userId")
        return client.get(ServiceProjectResource::class).update(
            userId = userId,
            projectId = projectId,
            projectUpdateInfo = projectUpdateInfo
        )
    }

    override fun get(
        appCode: String?,
        apigwType: String?,
        userId: String,
        projectId: String,
        accessToken: String?
    ): com.tencent.devops.project.pojo.Result<ProjectVO?> {
        logger.info("get project projectId($projectId) by user $userId")
        return client.get(ServiceProjectResource::class).get(
                englishName = projectId
        )
    }

    override fun list(
        appCode: String?,
        apigwType: String?,
        userId: String,
        accessToken: String?
    ): com.tencent.devops.project.pojo.Result<List<ProjectVO>> {
        logger.info("list project by user $userId")
        return client.get(ServiceProjectResource::class).list(
            userId = userId
        )
    }
}