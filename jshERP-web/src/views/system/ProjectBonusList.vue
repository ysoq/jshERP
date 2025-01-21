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
          <a-button v-if='btnEnableList.indexOf(2)>-1' icon='check' @click="batchSetStatus('1')">审核</a-button>
          <a-button v-if='btnEnableList.indexOf(7)>-1' icon='stop' @click="batchSetStatus('0')">反审核
          </a-button>
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
            :pagination='false'
            :scroll='scroll'
            :loading='loading'
            :rowSelection='{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }'
            @change='handleTableChange'
          >
            <span slot='action' slot-scope='text, record'>
                <a v-if="record.status!=='1'" @click='handleEdit(record)'>金额填写</a>
            </span>
            <template slot='customRenderStatus' slot-scope='status'>
              <a-tag v-if="status === '0'" color='red'>未审核</a-tag>
              <a-tag v-if="status === '1'" color='green'>已审核</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <ProjectBonusModal ref='projectBonus' @ok='loadData' />
      </a-card>
    </a-col>
  </a-row>
</template>
<!-- f r o m 7 5  2 7 1  8 9 2 0 -->
<script>
import ProjectBonusModal from './modules/ProjectBonusModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JDate from '@/components/jeecg/JDate'
import { getAction, postAction } from '@api/manage'
import { getProjectStatusText } from '@views/system/InOutItemCommon'
import { mapGetters } from 'vuex'
import dayjs from 'dayjs'

export default {
  name: 'InOutItemListView',
  mixins: [JeecgListMixin],
  components: {
    ProjectBonusModal,
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
      totalColumns: ['contractPrice', 'totalInAccount', 'totalUnInAccount', 'totalOutAccount'],
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
          title: '名称', dataIndex: 'name', width: 200
        },
        {
          title: '项目状态',
          dataIndex: 'projectStatusText',
          width: 60
        },
        {
          title: '合同金额', dataIndex: 'contractPrice', width: 150,
          customRender: (text, record, index) => {
            return this.getPrice2(record.contractPrice)
          }
        },
        { title: '项目经理', dataIndex: 'username', width: 100 },
        {
          title: '分配金额', dataIndex: 'totalPrice', width: 100,
          customRender: (text, record, index) => {
            return record.totalPrice ? this.getPrice2(record.totalPrice) : ''
          }
        },
        { title: '备注', dataIndex: 'remark', width: 100 },
        {
          title: '审核状态',
          dataIndex: 'status',
          width: 60,
          align: 'center',
          scopedSlots: { customRender: 'customRenderStatus' }
        }
      ],
      url: {
        list: '/inOutItem/projectBonus',
        batchSetStatusUrl: '/accountHead/batchSetStatus'
      }
    }
  },
  computed: {},
  methods: {
    ...mapGetters(['nickname']),
    loadData() {
      let params = this.getQueryParams() //查询条件
      this.loading = true
      return getAction(this.url.list, params).then((res) => {
        if (res.code === 200) {
          const list = []
          for (const item of res.data.bonus) {
            const account = res.data.account.filter(x => x.inOutItemId === item.id)[0]
            const status = res.data.status.filter(x => x.id === item.id)[0]
            list.push({
              ...item,
              projectStatusText: getProjectStatusText(status.projectStatus),
              totalPrice: account ? Math.abs(account.totalPrice) : null,
              status: account ? account.status : null,
              account,
              headerId: account ? account.id : null,
              remark: account ? account.remark : null,
            })
          }
          this.dataSource = list
          this.ipagination.total = 999
        } else if (res.code === 510) {
          this.$message.warning(res.data)
        } else {
          this.$message.warning(res.data.message)
        }
        this.loading = false
        this.onClearSelected()
      })
    },
    isAfterNow(row) {
      if (row.projectStatus === '5' || row.status === '1') {
        return false
      }
      return dayjs().isAfter(row.finishTime, 'day')
    },

    getPrice2(price) {
      const s = parseFloat(price)
      if (typeof s === 'number') {
        return this.formatPrice(s)
      }
      return ''
    },
    getPrice(record) {
      if (record.contractPrice >= 0) {
        if (record.totalInAccount > record.contractPrice) {
          return '0.00'
        } else {
          return this.formatPrice((record.contractPrice - record.totalInAccount))
        }
      }
      return ''
    },
    formatPrice(price) {
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
    handleEdit: function(record) {
      this.$refs.projectBonus.edit({
        ...record.account,
        inOutItemId: record.id
      })
      this.$refs.projectBonus.title = '编辑'
    },
    batchSetStatus: function(status) {
      if (this.selectionRows.length <= 0) {
        return this.$message.warning('请选择一条记录！')
      } else {
        if(this.selectionRows.some(x=> !x.headerId)) {
          return this.$message.warning('请先分配金额')
        }
        const ids = this.selectionRows.map(x => x.headerId).join(',')
        const that = this
        this.$confirm({
          title: '确认操作',
          content: '是否操作选中数据?',
          onOk: function() {
            that.loading = true
            postAction(that.url.batchSetStatusUrl, { status: status, ids: ids }).then((res) => {
              if (res.code === 200) {
                that.loadData()
              } else {
                that.$message.warning(res.data.message)
              }
            }).finally(() => {
              that.loading = false
            })
          }
        })
      }
    }

  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>