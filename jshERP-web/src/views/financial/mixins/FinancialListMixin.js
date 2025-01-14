import {
  findFinancialDetailByNumber, findBySelectSup, findBySelectCus, findBySelectOrgan, findBySelectRetail,
  getUserList, getPersonByType, getAccount, getCurrentSystemConfig, getPlatformConfigByKey, inOutItemExcludeFinish
} from '@/api/api'
import { getCheckFlag, getFormatDate, getPrevMonthFormatDate } from '@/utils/util'
import Vue from 'vue'
import moment from 'moment'
import dayjs from 'dayjs'

export const FinancialListMixin = {
  data() {

    let flowItem = window.localStorage.getItem('flowItem')
    if (flowItem) {
      flowItem = JSON.parse(flowItem)
      window.localStorage.removeItem('flowItem')
    }

    return {
      /* 原始审核是否开启 */
      checkFlag: true,
      /* 单据Excel是否开启 */
      isShowExcel: false,
      flowItem: flowItem,
      billExcelUrl: '',
      prefixNo: '',
      supList: [],
      cusList: [],
      organList: [],
      retailList: [],
      userList: [],
      personList: [],
      inOutList: [],
      accountList: [],
      queryParam: {
        beginTime: getPrevMonthFormatDate(3),
        endTime: getFormatDate(),
        createTimeRange: [moment(getPrevMonthFormatDate(3)), moment(getFormatDate())]
      },
      disableMixinCreated: true
    }
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },

    isBatchDelEnabled: function () {
      for (let i = 0; i < this.selectedRowKeys.length; i++) {
        if (!this.selectionRows[i].actionsEnabled.delete) {
          return false;
        }
      }
      return true;
    }
  },
  created() {
    this.isShowExcel = Vue.ls.get('isShowExcel');

    if (this.flowItem) {
      this.queryParam = {
        beginTime: dayjs(this.flowItem.remark).format('YYYY-MM-DD'),
        endTime: dayjs(this.flowItem.remark).format('YYYY-MM-DD'),
        billNo: this.flowItem.code,
        createTimeRange: []
      }
    }

    this.loadData();
    //初始化字典配置 在自己页面定义
    this.initDictConfig();
    //初始化按钮权限
    this.initActiveBtnStr();

    inOutItemExcludeFinish(false).then(list=> {
      this.inOutList = list
    })
  },
  methods: {
    loadDataAfter(list) {
      if (this.flowItem) {
        const item = list.filter(x => x.billNo === this.flowItem.code)[0]
        if (item) {
          this.myHandleDetail(item, this.flowItem.type, this.prefixNo)
        }
        this.flowItem = null
      }
    },
    myHandleAdd() {
      this.$refs.modalForm.action = "add";
      if (this.btnEnableList.indexOf(2) === -1) {
        this.$refs.modalForm.isCanCheck = false
      }
      this.handleAdd();
    },
    myHandleEdit(record) {
      if (record.status === '0') {
        this.$refs.modalForm.action = "edit";
        if (this.btnEnableList.indexOf(2) === -1) {
          this.$refs.modalForm.isCanCheck = false
        }
        //查询单条财务信息
        findFinancialDetailByNumber({ billNo: record.billNo }).then((res) => {
          if (res && res.code === 200) {
            let item = res.data
            this.handleEdit(item)
          }
        })
      } else {
        this.$message.warning("抱歉，只有未审核的单据才能编辑，请先进行反审核！")
      }
    },
    myHandleDelete(record) {
      if (record.status === '0') {
        this.handleDelete(record.id)
      } else {
        this.$message.warning("抱歉，只有未审核的单据才能删除，请先进行反审核！")
      }
    },
    myHandleDetail(record, type, prefixNo) {
      if (this.btnEnableList.indexOf(7) === -1) {
        this.$refs.modalDetail.isCanBackCheck = false
      }
      this.handleDetail(record, type, prefixNo);
    },
    handleApprove(record) {
      this.$refs.modalForm.action = "approve";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "审核";
    },
    searchReset() {
      this.queryParam = {
        type: this.queryParam.type,
        beginTime: getPrevMonthFormatDate(3),
        endTime: getFormatDate(),
        createTimeRange: [moment(getPrevMonthFormatDate(3)), moment(getFormatDate())]
      }
      this.loadData(1);
    },
    initSystemConfig() {
      getCurrentSystemConfig().then((res) => {
        if (res.code === 200 && res.data) {
          let multiBillType = res.data.multiBillType
          let multiLevelApprovalFlag = res.data.multiLevelApprovalFlag
          this.checkFlag = getCheckFlag(multiBillType, multiLevelApprovalFlag, this.prefixNo)
        }
      })
      getPlatformConfigByKey({ "platformKey": "bill_excel_url" }).then((res) => {
        if (res && res.code === 200) {
          if (res.data.platformValue) {
            this.billExcelUrl = res.data.platformValue
          }
        }
      })
    },
    initSupplier() {
      let that = this;
      findBySelectSup({}).then((res) => {
        if (res) {
          that.supList = res;
        }
      });
    },
    initCustomer() {
      let that = this;
      findBySelectCus({}).then((res) => {
        if (res) {
          that.cusList = res;
        }
      });
    },
    initOrgan() {
      let that = this;
      findBySelectOrgan({}).then((res) => {
        if (res) {
          that.organList = res;
        }
      });
    },
    initRetail() {
      let that = this;
      findBySelectRetail({}).then((res) => {
        if (res) {
          that.retailList = res;
        }
      });
    },
    initUser() {
      getUserList({}).then((res) => {
        if (res) {
          this.userList = res;
        }
      });
    },
    initPerson() {
      let that = this;
      getPersonByType({ type: '财务员' }).then((res) => {
        if (res && res.code === 200) {
          that.personList = res.data.personList;
        }
      })
    },
    initAccount() {
      getAccount({}).then((res) => {
        if (res && res.code === 200) {
          let list = res.data.accountList
          this.accountList = list
        }
      })
    },
    onDateChange: function (value, dateString) {
      this.queryParam.beginTime = dateString[0]
      this.queryParam.endTime = dateString[1]
      if (dateString[0] && dateString[1]) {
        this.queryParam.createTimeRange = [moment(dateString[0]), moment(dateString[1])]
      }
    },
    onDateOk(value) {
      console.log(value);
    },
    //导出单据
    handleExport() {
      let search = this.getQueryParams().search
      this.$refs.billExcelIframe.show(this.model, this.billExcelUrl + '?search=' + search + '&type=2', 150)
      this.$refs.billExcelIframe.title = "确认导出"
    }
  }
}