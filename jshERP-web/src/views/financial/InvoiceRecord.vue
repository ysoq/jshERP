<!-- by j i s h e n g hua -->
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :lg="5" :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目" data-step="1" data-title="项目">
                  <a-select placeholder="请选择项目" v-model="queryParam.inOutItemId" allowClear
                            :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                    <a-select-option v-for="(item,index) in inOutList" :key="index" :value="item.value">
                      {{ item.text }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :lg="5" :md="12" :sm="24">
                <a-form-item label="往来单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select
                    placeholder="请选择往来单位"
                    showSearch
                    optionFilterProp="children"
                    v-model="queryParam.organId"
                  >
                    <a-select-option v-for="(item, index) in organList" :key="index" :value="item.id">
                      {{ item.supplier }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :lg="5" :md="12" :sm="24">
                <a-form-item label="单据编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入单据编号" v-model="queryParam.billNo"></a-input>
                </a-form-item>
              </a-col>
              <a-col :lg="5" :md="12" :sm="24">
                <a-form-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-range-picker
                    style="width: 100%"
                    v-model="queryParam.createTimeRange"
                    format="YYYY-MM-DD"
                    :placeholder="['开始时间', '结束时间']"
                    @change="onDateChange"
                    @ok="onDateOk"
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
                  <a-form-item label="操作员" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                      placeholder="请选择操作员"
                      showSearch
                      optionFilterProp="children"
                      v-model="queryParam.creator"
                    >
                      <a-select-option v-for="(item, index) in userList" :key="index" :value="item.id">
                        {{ item.userName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="5" :sm="24">
                  <a-form-item label="收入账户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                      placeholder="请选择收入账户"
                      showSearch
                      optionFilterProp="children"
                      v-model="queryParam.accountId"
                    >
                      <a-select-option v-for="(item, index) in accountList" :key="index" :value="item.id">
                        {{ item.name }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="5" :sm="24">
                  <a-form-item label="单据状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择单据状态" v-model="queryParam.status">
                      <a-select-option value="0">未审核</a-select-option>
                      <a-select-option value="9" v-if="!checkFlag">审核中</a-select-option>
                      <a-select-option value="1">已审核</a-select-option>
                      <a-select-option value="2">作废</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="5" :sm="24">
                  <a-form-item label="单据备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入单据备注" v-model="queryParam.remark"></a-input>
                  </a-form-item>
                </a-col>
              </template>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click="myHandleAdd()" type="primary" icon="plus">新增
          </a-button>
          <a-button v-if="btnEnableList.indexOf(1) > -1" icon="delete" @click="batchDel">删除</a-button>
          <a-button v-if="checkFlag && btnEnableList.indexOf(2) > -1" icon="check" @click="batchSetStatus('1')"
          >审核
          </a-button
          >
          <a-button v-if="checkFlag && btnEnableList.indexOf(7) > -1" icon="stop" @click="batchSetStatus('0')"
          >反审核
          </a-button
          >
          <a-button v-if="checkFlag && btnEnableList.indexOf(2) > -1" icon="stop" @click="batchSetStatus('2')"
          >作废
          </a-button
          >
          <a-button v-if="btnEnableList.indexOf(3) > -1" icon="download" @click="handleExport"
          >导出
          </a-button
          >
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
            :components="handleDrag(columns)"
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <span slot="action" slot-scope="text, record">
              <a @click="myHandleDetail(record, '收入', prefixNo)">查看</a>
              <a-divider v-if="btnEnableList.indexOf(1) > -1" type="vertical" />
              <a v-if="btnEnableList.indexOf(1) > -1" @click="handleEdit(record)">编辑</a>
              <a-divider v-if="btnEnableList.indexOf(1) > -1" type="vertical" />
              <a-popconfirm
                v-if="btnEnableList.indexOf(1) > -1"
                title="确定删除吗?"
                @confirm="() => myHandleDelete(record)"
              >
                <a>删除</a>
              </a-popconfirm>
            </span>
            <template slot="customRenderStatus" slot-scope="status">
              <a-tag v-if="status == '0'" color="red">未审核</a-tag>
              <a-tag v-if="status == '1'" color="green">已审核</a-tag>
              <a-tag v-if="status == '2'" color="orange">作废</a-tag>
              <a-tag v-if="status == '9'" color="orange">审核中</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <invoice-record-modal ref="modalForm" @ok="modalFormOk" @close="modalFormClose"></invoice-record-modal>
        <financial-detail ref="modalDetail" @ok="modalFormOk" @close="modalFormClose"></financial-detail>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
import InvoiceRecordModal from './modules/InvoiceRecordModal'
import FinancialDetail from './dialog/FinancialDetail'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { FinancialListMixin } from './mixins/FinancialListMixin'
import JDate from '@/components/jeecg/JDate'
import { getAction, postAction } from '@api/manage'

export default {
  name: 'ItemInList',
  mixins: [JeecgListMixin, FinancialListMixin],
  components: {
    InvoiceRecordModal,
    FinancialDetail,
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
        billNo: '',
        inOutItemId: '',
        searchMaterial: '',
        type: '收入',
        organId: undefined,
        creator: undefined,
        handsPersonId: undefined,
        accountId: undefined,
        status: undefined,
        remark: ''
      },
      // 表头
      columns: [
        {
          title: '操作',
          dataIndex: 'action',
          width: 200,
          align: 'center',
          scopedSlots: { customRender: 'action' }
        },
        { title: '项目', dataIndex: 'projectId', width: 140, ellipsis: true },
        { title: '往来单位', dataIndex: 'supplierId', width: 140, ellipsis: true },
        { title: '单据编号', dataIndex: 'invoiceNumber', width: 160 },
        { title: '收入账户 ', dataIndex: 'accountId', width: 160 },
        { title: '开票含税金额', dataIndex: 'taxAmount', width: 100, ellipsis: true },
        { title: '单据日期', dataIndex: 'invoiceDate', width: 100, ellipsis: true },
        {
          title: '状态',
          dataIndex: 'status',
          width: 80,
          align: 'center',
          scopedSlots: { customRender: 'customRenderStatus' }
        },
        { title: '操作员', dataIndex: 'userName', width: 80 },
        { title: '审核员', dataIndex: 'auditor', width: 100 },
        { title: '审核时间', dataIndex: 'auditTime', width: 160 },
        { title: '备注', dataIndex: 'remark', width: 80 }
      ],
      url: {
        list (args) {
          return postAction('/api/invoiceRecord/findPage', args)
        },
        delete: '/accountHead/delete',
        deleteBatch: '/accountHead/deleteBatch',
        batchSetStatusUrl: '/accountHead/batchSetStatus'
      }
    }
  },
  computed: {},
  created () {
    this.initSystemConfig()
    this.initOrgan()
    this.initUser()
    this.initPerson()
    this.initAccount()
  },
  mounted () {

  },
  methods: {
    async handleEdit (record) {
      console.log(record, 'handleEdit')
      if (!record) {
        const res = await getAction(`/api/invoiceRecord/${record.id}`, {})
        this.$refs.modalForm.edit(res.data)
      } else {
        this.$refs.modalForm.edit({})
      }

      this.$refs.modalForm.title = '编辑'
      this.$refs.modalForm.disableSubmit = false
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>