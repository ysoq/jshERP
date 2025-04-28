<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="5" :sm="24">
                <a-form-item label="项目编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="" v-model="queryParam.projectCode"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="项目名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="" v-model="queryParam.projectName"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="班组" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="" v-model="queryParam.teamId" optionFilterProp="children"
                            :dropdownMatchSelectWidth="false" showSearch allowClear>
                    <a-select-option v-for="(item, index) in workTeamList" :key="index" :value="item.id">
                      {{ item.name }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <a-col :md="5" :sm="24">
                <a-form-item label="提交时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-range-picker
                    style="width:100%"
                    v-model="queryParam.createTimeRange"
                    format="YYYY-MM-DD"
                    valueFormat="YYYY-MM-DD"
                    :placeholder="['开始时间', '结束时间']"
                  />
                </a-form-item>
              </a-col>
              <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                <a-col :md="4" :sm="24">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
                  <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                  </a>
                </a-col>
              </span>
              <template v-if="toggleSearchStatus">

                <a-col :md="5" :sm="24">
                  <a-form-item label="审核时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-range-picker
                      style="width:100%"
                      v-model="queryParam.auditBeginRange"
                      format="YYYY-MM-DD"
                      valueFormat="YYYY-MM-DD"
                      :placeholder="['开始时间', '结束时间']"
                    />
                  </a-form-item>
                </a-col>
                <a-col :md="5" :sm="24">
                  <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="" v-model="queryParam.remark"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="5" :sm="24">
                  <a-form-item label="审核状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="审核状态" v-model="queryParam.status" allowClear optionFilterProp="children"
                              :dropdownMatchSelectWidth="false" showSearch>
                      <a-select-option value="0">未审核</a-select-option>
                      <a-select-option value="1">审核通过</a-select-option>
                      <a-select-option value="2">作废</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </template>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click="handleEdit({})" type="primary" icon="plus">新增
          </a-button>
          <a-button v-if=" btnEnableList.indexOf(2) > -1" icon="check" @click="batchSetStatus('1')">审核
          </a-button>
          <a-button v-if="btnEnableList.indexOf(7) > -1" icon="stop" @click="batchSetStatus('0')">反审核
          </a-button>
          <a-button v-if="btnEnableList.indexOf(2) > -1" icon="stop" @click="batchSetStatus('2')">作废
          </a-button>
        </div>
        <!-- table区域-begin -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="false"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <span slot="action" slot-scope="text, record">
              <template v-if="record.status!=='1'">
                  <a @click="handleEdit(record)">编辑</a>
                 <a-divider v-if="btnEnableList.indexOf(1) > -1" type="vertical" />
                  <a-popconfirm
                    v-if="btnEnableList.indexOf(1) > -1"
                    title="确定删除吗?"
                    @confirm="() => handleDelete(record.id)"
                  >
                  <a>删除</a>
                </a-popconfirm>
              </template>
            </span>
            <template slot="customRenderStatus" slot-scope="status">
              <a-tag v-if="status === '0'" color="red">未审核</a-tag>
              <a-tag v-if="status === '1'" color="green">已审核</a-tag>
              <a-tag v-if="status == '2'" color="orange">作废</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <ProjectBonusModal ref="projectBonus" @ok="loadData" :workTeamList="workTeamList"
                           :bonusList="bonusList"
        />
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
import ProjectBonusModal from './modules/ProjectBonusModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JDate from '@/components/jeecg/JDate'
import { getAction, postAction } from '@api/manage'
import { getProjectStatusText } from '@views/system/InOutItemCommon'
import dayjs from 'dayjs'
import { getProjectSelect, getUserList } from '@api/api'
import { getMetaValue } from '@/utils/util'

export default {
  name: 'ProjectBonusList',
  mixins: [JeecgListMixin],
  components: {
    ProjectBonusModal,
    JDate
  },
  data () {
    return {
      labelCol: {
        span: 5
      },
      wrapperCol: {
        span: 18,
        offset: 1
      },
      // 查询条件
      queryParam: {
        projectCode: '',
        projectName: '',
        teamId: '',
        createTimeRange: [],
        auditBeginRange: [],
        status: ''
      },
      // 表头
      columns: [
        {
          title: '操作',
          dataIndex: 'action',
          width: 180,
          align: 'center',
          scopedSlots: { customRender: 'action' }
        },
        {
          title: '项目编号', dataIndex: 'code', width: 100,
          customRender: (text, record, index) => {
            return getMetaValue(this.projectList, record.projectId, 'code', 'id')
          }
        },
        {
          title: '项目名称', dataIndex: 'name', width: 100,
          customRender: (text, record, index) => {
            return getMetaValue(this.projectList, record.projectId, 'name', 'id')
          }
        },
        {
          title: '班组', dataIndex: 'teamId', width: 100,
          customRender: (text, record, index) => {
            return getMetaValue(this.workTeamList, text, 'name', 'id')
          }
        },
        {
          title: '分配金额', dataIndex: 'amount', width: 100,
          customRender: (text, record, index) => {
            return text
          }
        },
        { title: '备注', dataIndex: 'remark', width: 100 },
        {
          title: '操作人', dataIndex: 'updater', width: 100,
          customRender: (text, record, index) => {
            return getMetaValue(this.userList, text, 'userName', 'id')
          }
        },
        { title: '提交时间', dataIndex: 'updateTime', width: 160 },
        { title: '审核人', dataIndex: 'auditor', width: 100 },
        { title: '审核时间', dataIndex: 'auditTime', width: 160 },
        {
          title: '状态', dataIndex: 'status', width: 80,
          align: 'center',
          scopedSlots: { customRender: 'customRenderStatus' }
        }
      ],
      url: {
        async list (args) {
          await this.beforeTask
          return postAction('/api/projectAmount/findPage', args)
        },
        delete: '/api/projectAmount/delete',
        batchSetStatusUrl: '/api/projectAmount/batchSetStatus',
        useStatusVerify: true
      },
      userList: [],
      projectList: [],
      workTeamList: [],
      status: [
        { value: '0', label: '未审核' },
        { value: '1', label: '已审核' },
        { value: '2', label: '作废' }
      ],
      bonusList: [],
      beforeTask: null
    }
  },
  computed: {},
  created () {
    this.beforeTask = Promise.all([
      getAction('/inOutItem/canBonus').then(res => {
        this.bonusList = res.data
      }),
      getAction('/api/workTeam/findBySelect').then(res => {
        this.workTeamList = res
      }),
      getProjectSelect('').then(res => {
        console.log('1111', res)
        this.projectList = res
      }),
      getUserList({}).then((res) => {
        if (res) {
          this.userList = res
        }
      })
    ])

  },
  methods: {
    handleEdit: function(record) {
      this.$refs.projectBonus.edit({
        ...record
      })
      this.$refs.projectBonus.title = record.id ? '编辑' : '新增'
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>