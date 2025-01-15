<!-- from 7 5 2 71 8 9 2 0 -->
<template>
  <a-row :gutter='24'>
    <a-col :md='24'>
      <a-card :style='cardStyle' :bordered='false'>
        <!-- 查询区域 -->
        <div class='table-page-search-wrapper'>
          <a-form layout='inline' @keyup.enter.native='searchQuery'>
            <a-row :gutter='24'>
              <a-col :lg='6' :md='12' :sm='24'>
                <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='项目' data-step='1' data-title='项目'>
                  <a-select placeholder='请选择项目' v-model='queryParam.inOutItemId' allowClear
                            :dropdownMatchSelectWidth='false' showSearch optionFilterProp='children'>
                    <a-select-option v-for='(item,index) in projectList' :key='index' :value='item.value'>
                      {{ item.text }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md='6' :sm='24'>
                <a-form-item label='单据日期' :labelCol='labelCol' :wrapperCol='wrapperCol'>
                  <a-range-picker
                    style='width: 100%'
                    v-model='queryParam.createTimeRange'
                    format='YYYY-MM-DD'
                    :placeholder="['开始时间', '结束时间']"
                    @change='onDateChange'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='6' :sm='24'>
                <a-form-item label='操作员' :labelCol='labelCol' :wrapperCol='wrapperCol'>
                  <a-select placeholder='请选择操作员' showSearch allow-clear optionFilterProp='children'
                            v-model='queryParam.creator'>
                    <a-select-option v-for='(item,index) in userList' :key='index' :value='item.id'>
                      {{ item.userName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md='6' :sm='24'>
                <span style='float: left;overflow: hidden;' class='table-page-search-submitButtons'>
                  <a-button type='primary' @click='searchQuery'>查询</a-button>
                  <a-button style='margin-left: 8px' v-print="'#reportPrint'" icon='printer'>打印</a-button>
                  <a-button style='margin-left: 8px' @click='exportExcel' icon='download'>导出</a-button>
                </span>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <!-- table区域-begin -->
        <section ref='print' id='reportPrint'>
          <a-table
            bordered
            ref='table'
            size='middle'
            rowKey='id'
            :columns='columns'
            :dataSource='dataSource'
            :components='handleDrag(columns)'
            :pagination='false'
            :scroll='scroll'
            :loading='loading'
            @change='handleTableChange'>
            <span slot='customTitle'>
              <a-popover trigger='click' placement='right'>
                <template slot='content'>
                  <a-checkbox-group @change='onColChange' v-model='settingDataIndex' :defaultValue='settingDataIndex'>
                    <a-row style='width: 600px'>
                      <template v-for='(item,index) in defColumns'>
                        <template>
                          <a-col :span='6'>
                            <a-checkbox :value='item.dataIndex' v-if="item.dataIndex==='rowIndex'"
                                        disabled></a-checkbox>
                            <a-checkbox :value='item.dataIndex' v-if="item.dataIndex!=='rowIndex'">
                              <j-ellipsis :value='item.title' :length='10'></j-ellipsis>
                            </a-checkbox>
                          </a-col>
                        </template>
                      </template>
                    </a-row>
                    <a-row style='padding-top: 10px;'>
                      <a-col>
                        恢复默认列配置：<a-button @click='handleRestDefault' type='link' size='small'>恢复默认</a-button>
                      </a-col>
                    </a-row>
                  </a-checkbox-group>
                </template>
                <a-icon type='setting' />
              </a-popover>
            </span>
            <span slot='numberCustomRender' slot-scope='text, record'>
              <a @click='myHandleDetail(record)'>{{ record.billNo }}</a>
            </span>
          </a-table>
          <a-row :gutter='24' style='margin-top: 8px;text-align:right;'>
            <a-col :md='24' :sm='24'>
              <a-pagination @change='paginationChange' @showSizeChange='paginationShowSizeChange'
                            size='small'
                            show-size-changer
                            :showQuickJumper='true'
                            :current='ipagination.current'
                            :page-size='ipagination.pageSize'
                            :page-size-options='ipagination.pageSizeOptions'
                            :total='ipagination.total'
                            :show-total='(total, range) => `共 ${total-Math.ceil(total/ipagination.pageSize)} 条`'>
                <template slot='buildOptionText' slot-scope='props'>
                  <span>{{ props.value - 1 }}条/页</span>
                </template>
              </a-pagination>
            </a-col>
          </a-row>
        </section>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <financial-detail ref="financialDetail"></financial-detail>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getFormatDate, getPrevMonthFormatDate } from '@/utils/util'
import { getAction } from '@/api/manage'
import {  findFinancialDetailByNumber, getUserList } from '@/api/api'
import JEllipsis from '@/components/jeecg/JEllipsis'
import moment from 'moment'
import Vue from 'vue'
import FinancialDetail from '@views/financial/dialog/FinancialDetail.vue'

export default {
  name: 'OutDetail',
  mixins: [JeecgListMixin],
  components: {
    FinancialDetail,
    JEllipsis
  },
  data() {
    let flowItem = JSON.parse( window.localStorage.getItem('flowItem') || "{}")
    window.localStorage.removeItem('flowItem')
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
        inOutItemId: flowItem.inOutItemId,
        beginTime: getPrevMonthFormatDate(3),
        endTime: getFormatDate(),
        createTimeRange: [moment(getPrevMonthFormatDate(3)), moment(getFormatDate())],
        creator: undefined,
      },
      ipagination: {
        pageSize: 11,
        pageSizeOptions: ['11', '21', '31', '101', '201', '301', '1001', '2001', '3001']
      },
      organList: [],
      depotList: [],
      userList: [],
      orgaTree: [],
      operNumberTotalStr: '0',
      allPriceTotalStr: '0',
      tabKey: '1',
      pageName: 'outDetail',
      // 默认索引
      defDataIndex: ['rowIndex', 'billNo', 'type', 'inOutItemId', 'eachAmount', 'billTime', 'username', 'remark',],
      // 默认列
      defColumns: [
        {
          dataIndex: 'rowIndex', width: 40, align: 'center', slots: { title: 'customTitle' },
          customRender: function(t, r, index) {
            return (t !== '合计') ? (parseInt(index) + 1) : t
          }
        },
        {
          title: '单据编号', dataIndex: 'billNo', width: 100,
          scopedSlots: { customRender: 'numberCustomRender' }
        },
        {
          title: '项目', dataIndex: 'inOutItemId', width: 80, ellipsis: true,
          customRender: function(t, r) {
            return Vue.prototype.getProjectName(r.inOutItemId)
          }
        },
        { title: '金额', dataIndex: 'eachAmount', sorter: (a, b) => a.eachAmount - b.eachAmount, width: 60 },
        { title: '类型', dataIndex: 'type', width: 70 },
        { title: '备注', dataIndex: 'remark', width: 100, ellipsis: true },
        { title: '日期', dataIndex: 'billTime', width: 100 },
        { title: '操作人员', dataIndex: 'username', width: 70 },
      ],
      url: {
        list: '/accountHead/getIncomeExpendDetail'
      }
    }
  },
  created() {
    this.initUser()
    this.initColumnsSetting()
  },
  methods: {
    moment,
    getQueryParams() {
      let param = Object.assign({}, this.queryParam, this.isorter)
      param.field = this.getQueryField()
      param.currentPage = this.ipagination.current
      param.pageSize = this.ipagination.pageSize - 1
      return param
    },
    onDateChange: function(value, dateString) {
      this.queryParam.beginTime = dateString[0]
      this.queryParam.endTime = dateString[1]
      if (dateString[0] && dateString[1]) {
        this.queryParam.createTimeRange = [moment(dateString[0]), moment(dateString[1])]
      }
    },
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1
      }
      let params = this.getQueryParams()//查询条件
      this.loading = true
      getAction(this.url.list, params).then((res) => {
        if (res.code === 200) {
          this.dataSource = res.data.rows
          this.ipagination.total = res.data.total
        } else if (res.code === 510) {
          this.$message.warning(res.data)
        } else {
          this.$message.warning(res.data.message)
        }
        this.loading = false
      })
    },


    initUser() {
      getUserList({}).then((res) => {
        if (res) {
          this.userList = res
        }
      })
    },

    myHandleDetail(record) {
      let that = this
      findFinancialDetailByNumber({ billNo: record.number }).then((res) => {
        if (res && res.code === 200) {
          this.$refs.financialDetail.isCanBackCheck = false
          that.$refs.financialDetail.show(res.data, record.type);
          that.$refs.financialDetail.title="详情";
        }
      })
    },
    searchQuery() {
      if (this.queryParam.beginTime == '' || this.queryParam.endTime == '') {
        this.$message.warning('请选择单据日期！')
      } else {
        this.loadData(1)
      }
    },
    exportExcel() {
      let list = []
      let head = '单据编号,项目,金额,类型,备注,日期,操作人员'
      for (let i = 0; i < this.dataSource.length; i++) {
        let item = []
        let ds = this.dataSource[i]
        item.push(ds.billNo, Vue.prototype.getProjectName(ds.inOutItemId), ds.eachAmount ,ds.type, ds.remark, ds.billTime, ds.username)
        list.push(item)
      }
      let tip = '单据日期：' + this.queryParam.beginTime + '~' + this.queryParam.endTime
      this.handleExportXlsPost('收支汇总', '收支汇总', head, tip, list)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>