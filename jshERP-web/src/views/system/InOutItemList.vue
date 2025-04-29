<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入" v-model="queryParam.name"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入" v-model="queryParam.code"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择" v-model="queryParam.type">
                    <a-select-option value="大包">大包</a-select-option>
                    <a-select-option value="清包">清包</a-select-option>
                    <a-select-option value="运维">运维</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                <a-col :md="6" :sm="24">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
                  <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                  </a>
                </a-col>
              </span>
              <template v-if="toggleSearchStatus">
                <a-col :md="6" :sm="24">
                  <a-form-item label="项目经理" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择" v-model="queryParam.manager" optionFilterProp="children"
                              :dropdownMatchSelectWidth="false" showSearch>
                      <a-select-option v-for="(item, index) in userList" :key="index" :value="item.id">
                        {{ item.userName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="客户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择" v-model="queryParam.supplierId" showSearch
                              optionFilterProp="children"
                              :dropdownMatchSelectWidth="false">
                      <a-select-option v-for="(item, index) in supplierList" :key="index" :value="item.id">
                        {{ item.supplier }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入" v-model="queryParam.remark"></a-input>
                  </a-form-item>
                </a-col>
              </template>


            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click="handleAdd" type="primary" icon="plus">新增</a-button>
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click="batchDel" icon="delete">删除</a-button>
          <a-button v-if="btnEnableList.indexOf(2)>-1" icon="check" @click="handleSetStatus('examine')">审核</a-button>
          <a-button v-if="btnEnableList.indexOf(7)>-1" icon="stop" @click="handleSetStatus('counter-audit')">反审核
          </a-button>
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click='handleSetStatus("enable")' icon="check-square"
          >启用
          </a-button
          >
          <a-button v-if="btnEnableList.indexOf(1) > -1" @click='handleSetStatus("disabled")' icon="close-square"
          >禁用
          </a-button
          >
          <a-button icon="download" @click="handleExport">导出</a-button>
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
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <span slot="action" slot-scope="text, record">
              <template v-if='record.rowIndex !== "合计"'>
              <a @click="handleMsg(record)" v-if='btnEnableList.indexOf(1) > -1 && record.status !== "1"'>进度填写</a>
              <a-divider v-if='btnEnableList.indexOf(1) > -1 && record.status !== "1"' type="vertical" />
              <a @click="handleEdit(record, false)" v-if='record.status !== "1"'>编辑</a>
              <a @click="handleEdit(record, true)" v-if='record.status === "1"'>查看</a>
              <a-divider v-if='btnEnableList.indexOf(1) > -1 && record.status !== "1"' type="vertical" />
              <a-popconfirm
                v-if='btnEnableList.indexOf(1) > -1 && record.status !== "1"'
                title="确定删除吗?"
                @confirm="() => handleDelete(record.id)"
              >
                <a>删除</a>
              </a-popconfirm>
            </template>

            </span>
            <span slot="name" slot-scope="name, record">
               <a @click="handleFlow(record)"> {{ name }}</a>
            </span>

            <div slot="totalInAccount" slot-scope="enabled, record">
              <span>{{ getPrice2(record.totalInAccount) }}</span>
              <span style="color: royalblue"
                    v-if="getPrice2(record.contractPrice ) && getPrice2(record.totalInAccount)"> ({{ (record.totalInAccount / record.contractPrice * 100).toFixed(2)
                }}%)</span>

            </div>

            <span slot="finishTime" slot-scope="finishTime, record">
              <span :style='{color: isAfterNow(record) ? "red": ""}'>{{ finishTime }}</span>
            </span>
            <!-- 状态渲染模板 -->
            <span slot="customRenderFlag" slot-scope="enabled,record">
              <template v-if='record.rowIndex !== "合计"'>

                <a-tag v-if="enabled" color="green">启用</a-tag>
                <a-tag v-if="!enabled" color="orange">禁用</a-tag>
              </template>
            </span>
            <span slot="projectStatus" slot-scope="projectStatus,record">
              <template v-if='record.rowIndex !== "合计"'>
                <a-tag
                  :color='record.status==="1" ? "green":"blue"'>{{ getProjectStatusText(projectStatus, record.status)
                  }}</a-tag>
              </template>
            </span>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <inOutItem-modal ref="modalForm" @ok="handleFormOk"></inOutItem-modal>
        <InOutFlowModal ref="flowModal"></InOutFlowModal>
        <InOutMsgModal ref="msg" @ok="modalFormOk" />
      </a-card>
    </a-col>
  </a-row>
</template>
<!-- f r o m 7 5  2 7 1  8 9 2 0 -->
<script>
import InOutItemModal from './modules/InOutItemModal'
import InOutFlowModal from './modules/InOutFlowModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JDate from '@/components/jeecg/JDate'
import InOutMsgModal from '@views/system/modules/InOutMsgModal.vue'
import { getAction, postAction } from '@api/manage'
import { getProjectStatusText } from '@views/system/InOutItemCommon'
import { mapGetters } from 'vuex'
import dayjs from 'dayjs'
import { getUserList } from '@api/api'

export default {
  name: 'InOutItemListView',
  mixins: [JeecgListMixin],
  components: {
    InOutItemModal,
    InOutFlowModal,
    InOutMsgModal,
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
      userList: [],
      supplierList: [],
      msgList: [],
      // 查询条件
      queryParam: { name: '', type: '', remark: '' },
      totalColumns: ['contractPrice', 'totalInAccount', 'totalUnInAccount', 'totalOutAccount', 'taxAmount'],
      // 表头
      columns: [
        {
          dataIndex: 'rowIndex', width: 60, align: 'center', slots: { title: 'customTitle' },
          customRender: function(t, r, index) {
            return (t !== '合计') ? (parseInt(index) + 1) : t
          }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 180,
          align: 'center',
          scopedSlots: { customRender: 'action' }
        },
        { title: '编号', dataIndex: 'code', width: 150 },
        {
          title: '名称', dataIndex: 'name', width: 200,
          scopedSlots: { customRender: 'name' }
        },
        { title: '类型', dataIndex: 'type', width: 100 },
        { title: '项目经理', dataIndex: 'username', width: 100 },
        { title: '联系方式', dataIndex: 'phonenum', width: 130 },
        { title: '客户', dataIndex: 'supplierName', width: 150 },
        {
          title: '项目完成日期', dataIndex: 'finishTime', width: 150,
          scopedSlots: { customRender: 'finishTime' }
        },
        {
          title: '项目进度', dataIndex: 'projectStatus', width: 100,
          align: 'center',
          scopedSlots: { customRender: 'projectStatus' }
        },
        {
          title: '合同金额', dataIndex: 'contractPrice', width: 150,
          customRender: (text) => {
            return this.getPrice2(text)
          }
        },
        {
          title: '含税开票金额', dataIndex: 'taxAmount', width: 150,
          customRender: (text) => {
            return this.getPrice2(text)
          }
        },
        {
          title: '已回款金额', dataIndex: 'totalInAccount', width: 180,
          scopedSlots: { customRender: 'totalInAccount' }
        },
        {
          title: '未回款金额',
          dataIndex: 'totalUnInAccount',
          width: 150,
          customRender: (text, item) => {
            return this.getPrice(item)
          }
        },
        {
          title: '支出金额', dataIndex: 'totalOutAccount', width: 150,
          customRender: (text) => {
            return this.getPrice2(text)
          }
        },

        {
          title: '状态',
          dataIndex: 'enabled',
          width: 60,
          align: 'center',
          scopedSlots: { customRender: 'customRenderFlag' }
        },
        { title: '备注', dataIndex: 'remark', width: 150 }

      ],
      url: {
        list: async (params) => {
          await this.taskList
          const list = await getAction('/inOutItem/list', params)
          for (const item of list.data.rows) {
            item.msgList = this.msgList ? this.msgList.filter(x => x.inOutItemId === item.id) : []
            item.projectStatus = (item.msgList[0] || { projectStatus: '1' }).projectStatus
            item.totalOutAccount = (item.totalOutAccount || 0) + (item.projectAmount || 0)
          }

          function getTotal (key) {
            return list.data.rows.reduce((a, b) => a + b[key], 0).toFixed(2)
          }

          list.data.rows.push({
            rowIndex: '合计',
            contractPrice: getTotal('contractPrice'),
            totalInAccount: getTotal('totalInAccount'),
            totalOutAccount: getTotal('totalOutAccount')
          })
          return list
        },
        delete: '/inOutItem/delete',
        deleteBatch: '/inOutItem/deleteBatch',
        batchSetStatusUrl: '/inOutItem/batchSetStatus'
      }
    }
  },
  created () {
    const args = {
      search: `{"supplier":"","type":"客户","telephone":"","phonenum":""}`,
      currentPage: 1,
      pageSize: 1000,
      column: 'createTime',
      order: 'desc'
    }
    this.taskList = Promise.all([
      getUserList({}).then((res) => {
        if (res) {
          this.userList = res
        }
      }),
      getAction('/supplier/list', args).then(res => {
        this.supplierList = res.data.rows
      }),
      getAction('/msg/getMsgCountByType', { 'type': '项目进度' }).then(res => {
        this.msgList = res.data.list
      })
    ])

  },
  computed: {},
  methods: {
    ...mapGetters(['nickname']),
    getProjectStatusText,
    handleSetStatus (status) {
      if (!this.selectionRows.every(x => x.projectStatus === '5' && x.status !== '1') && status === 'examine') {
        this.$message.error(`只能选择${getProjectStatusText('5')}状态数据`)
        return
      } else if (status === 'counter-audit' && !this.selectionRows.every(x => x.status === '1')) {
        this.$message.error(`只能选择${getProjectStatusText(1, '1')}状态数据`)
        return
      }

      this.batchSetStatus(status).then(async () => {
        for (const item of this.selectionRows) {
          if (status === 'examine') {
            await this.addMsg(`由${this.nickname()}审核`, item.id, '6')
          } else if (status === 'counter-audit') {
            await this.addMsg(`由${this.nickname()}反审核`, item.id, '5')
          }
        }
        await this.modalFormOk()
      })
    },
    isAfterNow (row) {
      if (row.projectStatus === '5' || row.status === '1') {
        return false
      }
      return dayjs().isAfter(row.finishTime, 'day')
    },
    async handleFormOk (type) {
      await this.modalFormOk()
      if (type === 'insert') {
        const item = this.dataSource[0]
        await this.addMsg(`项目创建`, item.id, '1')
        await this.modalFormOk()
      }
    },
    addMsg (title, id, projectStatus) {
      let msgParam = {
        msgTitle: title,
        msgContent: '',
        inOutItemId: id,
        projectStatus: projectStatus,
        recoverFile: '',
        type: '项目进度'
      }
      return postAction('/msg/add', msgParam)
    },
    getPrice2 (price) {
      const s = parseFloat(price)
      if (typeof s === 'number' && Number.isFinite(s)) {
        return this.formatPrice(s)
      }

      return null
    },
    getPrice (record) {
      if (record.contractPrice >= 0) {
        if (record.totalInAccount > record.contractPrice) {
          return '0.00'
        } else {
          return this.formatPrice((record.contractPrice - record.totalInAccount))
        }
      }
      return ''
    },
    formatPrice (price) {
      const num = parseFloat(price)
      if (typeof num === 'number') {
        const amount = num.toFixed(2)
        // 将金额转换为字符串，并分割为整数部分和小数部分
        let [integerPart, decimalPart] = amount.toString().split('.')

        // 对整数部分进行分组，每三位一组，从右向左
        integerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',')

        // 如果存在小数部分，则保留；否则不显示小数部分
        if (decimalPart) {
          return `${integerPart}.${decimalPart}`
        } else {
          return integerPart
        }

      }
      return price
    },
    handleEdit: function(record, disableSubmit) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = disableSubmit ? '查看' : '编辑'
      this.$refs.modalForm.disableSubmit = !!disableSubmit
      if (this.btnEnableList.indexOf(1) === -1) {
        this.$refs.modalForm.isReadOnly = true
      }
    },
    handleFlow (record) {
      this.$refs.flowModal.edit({
        ...record,
        msgList: record.msgList || []
      })
    },
    handleMsg (record) {
      this.$refs.msg.add({
        username: record.username,
        manager: record.manager,
        inOutItemId: record.id,
        projectStatus: record.projectStatus
      })
    },
    //导出单据
    handleExport () {
      let list = []
      let head = '#,编号,名称,类型,项目经理,联系方式,客户,项目完成日期,项目进度,合同金额,含税开票金额,已回款金额,未回款金额,支出金额,状态,备注'
      for (let i = 0; i < this.dataSource.length; i++) {
        let item = []
        let ds = this.dataSource[i]
        const status = ds.enabled ? '启用' : ds.enabled === false ? '禁用' : ''
        item.push(ds.rowIndex, ds.code, ds.name,
          ds.type, ds.username, ds.phonenum,
          ds.supplierName, ds.finishTimeStr, ds.projectStatus,
          this.getPrice2(ds.contractPrice),
          this.getPrice2(ds.taxAmount),
          this.getPrice2(ds.totalInAccount),
          this.getPrice(ds),
          this.getPrice2(ds.totalOutAccount),
          status, ds.remark)
        list.push(item)
      }
      let tip = '项目管理'
      this.handleExportXlsPost(tip, tip, head, tip, list)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>