<template>
  <a-row :gutter='24'>
    <a-col :md='24'>
      <a-card :style='cardStyle' :bordered='false'>
        <!-- 查询区域 -->
        <div class='table-page-search-wrapper'>
          <!-- 搜索区域 -->
          <a-form layout='inline' @keyup.enter.native='searchQuery'>
            <a-row :gutter='24'>
              <a-col :md='6' :sm='24'>
                <a-form-item label='名称' :labelCol='labelCol' :wrapperCol='wrapperCol'>
                  <a-input placeholder='请输入名称查询' v-model='queryParam.name'></a-input>
                </a-form-item>
              </a-col>
              <a-col :md='6' :sm='24'>
                <a-form-item label='备注' :labelCol='labelCol' :wrapperCol='wrapperCol'>
                  <a-input placeholder='请输入备注查询' v-model='queryParam.remark'></a-input>
                </a-form-item>
              </a-col>
              <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
                <a-col :md='6' :sm='24'>
                  <a-button type='primary' @click='searchQuery'>查询</a-button>
                  <a-button style='margin-left: 8px' @click='searchReset'>重置</a-button>
                </a-col>
              </span>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class='table-operator' style='margin-top: 5px'>
          <a-button v-if='btnEnableList.indexOf(1) > -1' @click='handleAdd' type='primary' icon='plus'>新增</a-button>
          <a-button v-if='btnEnableList.indexOf(1) > -1' @click='batchDel' icon='delete'>删除</a-button>
          <a-button v-if='btnEnableList.indexOf(1) > -1' @click='batchSetStatus(true)' icon='check-square'
          >启用
          </a-button
          >
          <a-button v-if='btnEnableList.indexOf(1) > -1' @click='batchSetStatus(false)' icon='close-square'
          >禁用
          </a-button
          >
        </div>
        <!-- table区域-begin -->
        <div>
          <a-table
            ref='table'
            size='middle'
            bordered
            rowKey='id'
            :columns='columns'
            :dataSource='dataSource'
            :pagination='ipagination'
            :scroll='scroll'
            :loading='loading'
            :rowSelection='{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }'
            @change='handleTableChange'
          >
            <span slot='action' slot-scope='text, record'>
              <a @click='handleMsg(record)'>进度填写</a>
              <a-divider v-if='btnEnableList.indexOf(1) > -1' type='vertical' />
              <a @click='handleEdit(record)'>编辑</a>
              <a-divider v-if='btnEnableList.indexOf(1) > -1' type='vertical' />
              <a-popconfirm
                v-if='btnEnableList.indexOf(1) > -1'
                title='确定删除吗?'
                @confirm='() => handleDelete(record.id)'
              >
                <a>删除</a>
              </a-popconfirm>
            </span>
            <span slot='name' slot-scope='name, record'>
               <a @click='handleFlow(record)'> {{ name }}</a>
            </span>
            <span slot='totalUnInAccount' slot-scope='enabled, record'>
              {{ getPrice(record) }}
            </span>
            <!-- 状态渲染模板 -->
            <template slot='customRenderFlag' slot-scope='enabled'>
              <a-tag v-if='enabled' color='green'>启用</a-tag>
              <a-tag v-if='!enabled' color='orange'>禁用</a-tag>
            </template>
            <template slot='projectStatus' slot-scope='projectStatus'>
              <a-tag v-if="projectStatus==='2'" color='green'>已完成</a-tag>
              <a-tag v-else color='orange'>进行中</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <inOutItem-modal ref='modalForm' @ok='modalFormOk'></inOutItem-modal>
        <InOutFlowModal ref='flowModal'></InOutFlowModal>
        <InOutMsgModal ref='msg' @ok='modalFormOk'/>
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
import { getAction } from '@api/manage'

export default {
  name: 'InOutItemList',
  mixins: [JeecgListMixin],
  components: {
    InOutItemModal,
    InOutFlowModal,
    InOutMsgModal,
    JDate
  },
  data() {
    return {
      labelCol: {
        span: 5
      },
      wrapperCol: {
        span: 18,
        offset: 1
      },
      // 查询条件
      queryParam: { name: '', type: '', remark: '' },
      // 表头
      columns: [
        { title: '编号', dataIndex: 'code', width: 50 },
        {
          title: '操作',
          dataIndex: 'action',
          width: 150,
          align: 'center',
          scopedSlots: { customRender: 'action' }
        },
        {
          title: '名称', dataIndex: 'name', width: 200,
          scopedSlots: { customRender: 'name' }
        },
        { title: '项目经理', dataIndex: 'username', width: 100 },
        { title: '联系方式', dataIndex: 'phonenum', width: 100 },
        { title: '合同金额', dataIndex: 'contractPrice', width: 100 },
        { title: '已回款金额', dataIndex: 'totalInAccount', width: 100 },
        {
          title: '未回款金额',
          dataIndex: 'totalUnInAccount',
          width: 100,
          scopedSlots: { customRender: 'totalUnInAccount' }
        },
        { title: '支出金额', dataIndex: 'totalOutAccount', width: 100 },
        {
          title: '项目状态', dataIndex: 'projectStatus', width: 100,
          align: 'center',
          scopedSlots: { customRender: 'projectStatus' }
        },
        { title: '备注', dataIndex: 'remark', width: 200 },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: 60,
          align: 'center',
          scopedSlots: { customRender: 'customRenderFlag' }
        }
      ],
      url: {
        async list(params) {
          const msgList = await getAction('/msg/getMsgCountByType', { 'type': '项目进度' }).then(res => {
            return res.data.list
          })
          const list = await getAction('/inOutItem/list', params)
          for (const item of list.data.rows) {
            item.msgList = msgList.filter(x => x.inOutItemId === item.id)
            item.projectStatus = (item.msgList[0] || { projectStatus: '1' }).projectStatus
            item.projectStatusText = item.projectStatus === '2' ? '已完成' : '进行中'
          }
          return list
        },
        delete: '/inOutItem/delete',
        deleteBatch: '/inOutItem/deleteBatch',
        batchSetStatusUrl: '/inOutItem/batchSetStatus'
      }
    }
  },
  computed: {},
  methods: {
    getPrice(record) {
      if (record.contractPrice >= 0) {
        if (record.totalInAccount > record.contractPrice) {
          return record.contractPrice
        } else {
          return (record.contractPrice - record.totalInAccount).toFixed(2)
        }
      }
      return ''
    },
    handleEdit: function(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
      this.$refs.modalForm.disableSubmit = false
      if (this.btnEnableList.indexOf(1) === -1) {
        this.$refs.modalForm.isReadOnly = true
      }
    },
    handleFlow(record) {
      this.$refs.flowModal.edit({
        ...record,
        msgList: record.msgList || []
      })
    },
    handleMsg(record) {
      this.$refs.msg.add({
        username: record.username,
        manager: record.manager,
        inOutItemId: record.id,
        projectStatus: record.projectStatus
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>