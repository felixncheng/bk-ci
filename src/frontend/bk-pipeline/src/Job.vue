<template>
    <div>
        <h3 :class="jobTitleCls"
            @click.stop="showContainerPanel"
        >
            <status-icon type="container" :editable="editable" :container-disabled="disabled" :status="containerStatus" :depend-on-value="dependOnValue">
                {{ containerSerialNum }}
            </status-icon>
            <p class="container-name">
                <span
                    :class="displayNameCls"
                    :title="displayName"
                >
                    {{ displayName }}
                </span>
            </p>
            <container-type :class="containerTypeCls" :container="container" v-if="!canSkipElement"></container-type>
            <Logo
                v-if="showCopyJob && !container.isError"
                :title="t('copyJob')"
                class="copyJob"
                @click.stop="handleCopyContainer"
                name="clipboard"
                size="16"
            />
            <i v-if="showCopyJob" @click.stop="deleteJob" class="add-plus-icon close" />
            <span @click.stop v-if="canSkipElement">
                <bk-checkbox class="atom-canskip-checkbox" v-model="container.runContainer" :disabled="disabled"></bk-checkbox>
            </span>
            <Logo
                v-if="(editable || isPreview) && container.matrixGroupFlag"
                name="matrix"
                size="16"
                class="matrix-flag-icon"
            >
            </Logo>
            <Logo
                v-if="showMatrixFold"
                name="angle-circle-down"
                size="18"
                @click.stop="toggleShowAtom"
                :class="matrixFoldLogoCls"
            >
            </Logo>
            <bk-button v-if="showDebugBtn" class="debug-btn" theme="warning" @click.stop="debugDocker">{{ $t('editPage.docker.debugConsole') }}</bk-button>
        </h3>
        <atom-list
            v-if="showAtomList || !showMatrixFold"
            :stage="stage"
            :container="container"
            :editable="editable"
            :is-preview="isPreview"
            :can-skip-element="canSkipElement"
            :stage-index="stageIndex"
            :handle-change="handleChange"
            :cancel-user-id="cancelUserId"
            :user-name="userName"
            :container-index="containerIndex"
            :container-group-index="containerGroupIndex"
            :container-status="containerStatus"
            :match-rules="matchRules"
            :container-disabled="disabled"
        >
        </atom-list>
    </div>
</template>

<script>
    import {
        hashID,
        randomString,
        eventBus,
        isTriggerContainer,
        getDependOnDesc,
        isObject
    } from './util'
    import { localeMixins } from './locale'
    
    import {
        DELETE_EVENT_NAME,
        COPY_EVENT_NAME,
        CLICK_EVENT_NAME,
        DEBUG_CONTAINER,
        STATUS_MAP,
        DOCKER_BUILD_TYPE,
        PUBLIC_DEVCLOUD_BUILD_TYPE,
        PUBLIC_BCS_BUILD_TYPE
    } from './constants'
    import ContainerType from './ContainerType'
    import AtomList from './AtomList'
    import StatusIcon from './StatusIcon'
    import Logo from './Logo'

    export default {
        components: {
            StatusIcon,
            ContainerType,
            AtomList,
            Logo
        },
        mixins: [localeMixins],
        props: {
            stage: {
                type: Object,
                requiured: true
            },
            container: {
                type: Object,
                requiured: true
            },
            stageIndex: Number,
            containerIndex: Number,
            containerGroupIndex: Number,
            containerLength: Number,
            stageDisabled: Boolean,
            disabled: Boolean,
            editable: {
                type: Boolean,
                default: true
            },
            isLatestBuild: {
                type: Boolean,
                default: false
            },
            isExecDetail: {
                type: Boolean,
                default: false
            },
            isPreview: {
                type: Boolean,
                default: false
            },
            canSkipElement: {
                type: Boolean,
                default: false
            },
            handleChange: {
                type: Function,
                required: true
            },
            cancelUserId: {
                type: String,
                default: 'unknow'
            },
            userName: {
                type: String,
                default: 'unknow'
            },
            matchRules: {
                type: Array,
                default: () => []
            },
            stageLength: Number,
            updateCruveConnectHeight: Function
        },
        emits: [
            DELETE_EVENT_NAME,
            COPY_EVENT_NAME
        ],
        data () {
            return {
                showContainerName: false,
                showAtomName: false,
                showAtomList: false,
                cruveHeight: 0
            }
        },
        computed: {
            containerStatus () {
                return this.container && this.container.status ? this.container.status : ''
            },
            containerTypeCls () {
                return { 'hover-hide': this.showCopyJob }
            },
            jobTitleCls () {
                return {
                    'container-title': true,
                    'first-ctitle': this.containerIndex === 0,
                    DISABLED: this.disabled,
                    [this.containerStatus]: true
                }
            },
            displayNameCls () {
                return {
                    'skip-name': this.disabled || this.containerStatus === STATUS_MAP.SKIP
                }
            },
            matrixFoldLogoCls () {
                return {
                    'fold-atom-icon': true,
                    open: this.showAtomList,
                    [this.containerStatus || 'readonly']: true
                }
            },
            displayName () {
                try {
                    const { matrixContext, status, name } = this.container
                    const suffix = isObject(matrixContext) ? Object.values(matrixContext).join(', ') : ''
                    const isPrepare = (status === STATUS_MAP.PREPARE_ENV && this.containerGroupIndex === undefined)
                    return isPrepare ? this.t('prepareEnv') : `${name}${suffix ? `(${suffix})` : ''}`
                } catch (error) {
                    return 'unknow'
                }
            },
            showCopyJob () {
                return !isTriggerContainer(this.container) && this.editable
            },
            containerSerialNum () {
                return `${this.stageIndex + 1}-${this.containerIndex + 1}`
            },
            isOnlyOneContainer () {
                return this.containerLength === 1
            },
            projectId () {
                return this.$route.params.projectId
            },
            
            dependOnValue () {
                if (isTriggerContainer(this.container)) return ''
                const val = getDependOnDesc(this.container)
                return `${this.t('dependOn')} 【${val}】`
            },
            showMatrixFold () {
                return this.isExecDetail && this.containerGroupIndex !== undefined
            },
            buildResourceType () {
                try {
                    return this.container.dispatchType.buildType
                } catch (e) {
                    return DOCKER_BUILD_TYPE
                }
            },
            showDebugBtn () {
                const { isLatestBuild, isExecDetail, container: { baseOS, status } } = this
                const isshowDebugType = [DOCKER_BUILD_TYPE, PUBLIC_DEVCLOUD_BUILD_TYPE, PUBLIC_BCS_BUILD_TYPE].includes(this.buildResourceType)
                return baseOS === 'LINUX' && isshowDebugType && isExecDetail && isLatestBuild && status === STATUS_MAP.FAILED
            }
        },
        watch: {
            'container.runContainer' (newVal) {
                const { elements } = this.container
                if (this.disabled && newVal) return
                elements.filter(item => (item.additionalOptions === undefined || item.additionalOptions.enable))
                    .forEach(item => {
                        item.canElementSkip = newVal
                    })
                this.handleChange(this.container, { elements })
            }
        },
        mounted () {
            if (this.disabled) {
                this.handleChange(this.container, { runContainer: false })
            }
        },
        methods: {
            toggleShowAtom () {
                this.showAtomList = !this.showAtomList
                this.updateCruveConnectHeight()
            },
            deleteJob () {
                const { containerIndex, stageIndex, isOnlyOneContainer } = this
                
                this.$emit(DELETE_EVENT_NAME, {
                    stageIndex,
                    containerIndex: isOnlyOneContainer ? undefined : containerIndex
                })
            },
            showContainerPanel () {
                eventBus.$emit(CLICK_EVENT_NAME, {
                    stageIndex: this.stageIndex,
                    containerIndex: this.containerIndex,
                    containerGroupIndex: this.containerGroupIndex,
                    container: this.container
                })
            },
            
            handleCopyContainer () {
                try {
                    const copyContainer = JSON.parse(JSON.stringify(this.container))
                    const { containerHashId, containerId, ...resetContainerProps } = copyContainer
                    const container = {
                        ...resetContainerProps,
                        containerId: `c-${hashID()}`,
                        jobId: `job_${randomString(3)}`,
                        elements: copyContainer.elements.map(element => ({
                            ...element,
                            id: `e-${hashID()}`
                        })),
                        jobControlOption: copyContainer.jobControlOption
                            ? {
                                ...copyContainer.jobControlOption,
                                dependOnType: 'ID',
                                dependOnId: []
                            }
                            : undefined
                    }
                    this.$emit(COPY_EVENT_NAME, {
                        containerIndex: this.containerIndex,
                        container
                    })
                } catch (e) {
                    console.error(e)
                    this.$showTips({
                        theme: 'error',
                        message: this.t('copyJobFail')
                    })
                }
            },
            debugDocker () {
                eventBus.$emit(DEBUG_CONTAINER, {
                    stageIndex: this.stageIndex,
                    containerIndex: this.containerIndex,
                    containerGroupIndex: this.containerGroupIndex,
                    container: this.container
                })
            }
        }
    }
</script>

<style lang="scss">
    @use "sass:math";
    @import "./conf";
    .devops-stage-container {
        .container-title {
            display: flex;
            height: $itemHeight;
            background: #33333f;
            cursor: pointer;
            color: white;
            font-size: 14px;
            align-items: center;
            position: relative;
            margin: 0 0 16px 0;
            z-index: 3;
            > .container-name {
                @include ellipsis();
                flex: 1;
                padding: 0 6px;
            }

            .atom-canskip-checkbox {
                margin-right: 6px;
                &.is-disabled .bk-checkbox {
                    background-color: transparent;
                    border-color: #979BA4;
                }

            }
            input[type=checkbox] {
                border-radius: 3px;
            }
            .matrix-flag-icon {
                position: absolute;
                top: 0px;
                font-size: 16px;
            }
            .fold-atom-icon {
                position: absolute;
                background: white;
                border-radius: 50%;
                bottom: -10px;
                left: 44%;
                transition: all .3s ease;
                &.open {
                    transform: rotate(-180deg);
                }
                &.readonly {
                  color: $fontWeightColor;
                }
            }
            .copyJob {
                display: none;
                margin-right: 10px;
                color: $fontLighterColor;
                cursor: pointer;
                &:hover {
                    color: $primaryColor;
                }
            }
            .close {
                @include add-plus-icon(#2E2E3A, #2E2E3A, #c4c6cd, 16px, true);
                @include add-plus-icon-hover($dangerColor, $dangerColor, white);
                border: none;
                display: none;
                margin-right: 10px;
                transform: rotate(45deg);
                cursor: pointer;
                &:before, &:after {
                    left: 7px;
                    top: 4px;
                }
            }
            .debug-btn {
                position: absolute;
                height: 100%;
                right: 0;
            }

            &:hover {
                .copyJob, .close {
                    display: block;
                }
                .hover-hide {
                    display: none;
                }
            }

        }

    }
</style>
