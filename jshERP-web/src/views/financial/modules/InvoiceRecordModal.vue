<template>
  <j-modal :title="title" :width="width" :visible="visible" :confirmLoading="confirmLoading" :keyboard="false"
    :forceRender="true" fullscreen switchFullscreen @cancel="handleCancel" :id="prefixNo" style="top:20px;height: 95%;">
    <template slot="footer">
      <a-button @click="handleCancel">取消</a-button>
      <a-button v-if="checkFlag && isCanCheck" :loading="confirmLoading" @click="handleOkAndCheck">保存并审核</a-button>
      <a-button type="primary" :loading="confirmLoading" @click="handleOk">保存（Ctrl+S）</a-button>
      <!--发起多级审核-->
      <a-button v-if="!checkFlag" @click="handleWorkflow()" type="primary">提交流程</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目" data-step="1" data-title="项目">
              <a-select placeholder="请选择项目" v-decorator="['projectId', validatorRules.projectId]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <a-select-option v-for="(item, index) in inOutList" :key="index" :value="item.value">
                  {{ item.text }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="往来单位">
              <a-select placeholder="请选择往来单位" v-decorator="['supplierId', validatorRules.organId]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <a-select-option v-for="(item, index) in organList" :key="index" :value="item.id">
                  {{ item.supplier }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
              <j-date v-decorator="['invoiceDate', validatorRules.billTime]" :show-time="true" />
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="发票编号">
              <a-input placeholder="请输入发票编号" v-decorator.trim="['invoiceNumber']" />
            </a-form-item>
          </a-col>
        </a-row>
        <j-editable-table :ref="refKeys[0]" :loading="accountTable.loading" :columns="accountTable.columns"
          :dataSource="accountTable.dataSource" :minWidth="minWidth" :maxHeight="300" :rowNumber="true"
          :rowSelection="true" :actionButton="true" @added="onAdded" @deleted="onDeleted" @valueChange="onValueChange">
          <template #buttonAfter>
            <a-row :gutter="24" style="float:left;padding-bottom: 5px;padding-left:20px;">
              <a-button icon="import" @click="onImport()">导入明细</a-button>
            </a-row>
          </template>
        </j-editable-table>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="{ xs: { span: 24 }, sm: { span: 24 } }" label="">
              <a-textarea :rows="2" placeholder="请输入备注" v-decorator="['remark']" style="margin-top:8px;" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="收入账户">
              <a-select placeholder="请选择收入账户" v-decorator="['accountId', validatorRules.accountId]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <a-select-option v-for="(item, index) in accountList" :key="index" :value="item.id">
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开票含税金额">
              <a-input placeholder="" v-decorator.trim="['taxAmount', validatorRules.changeAmount]" :readOnly="true" />
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件">
              <j-upload v-model="fileList" bizPath="financial"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <import-item-modal ref="importItemModalForm" @ok="importItemModalFormOk"></import-item-modal>
  </j-modal>
</template>
<script>
import AccountModal from '../../system/modules/AccountModal'
import PersonModal from '../../system/modules/PersonModal'
import WorkflowIframe from '@/components/tools/WorkflowIframe'
import InOutItemModal from '../../system/modules/InOutItemModal'
import { FormTypes } from '@/utils/JEditableTableUtil'
import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
import { FinancialModalMixin } from '../mixins/FinancialModalMixin'
import JUpload from '@/components/jeecg/JUpload'
import JDate from '@/components/jeecg/JDate'
import { getAction } from '@api/manage'
import { getNowFormatDateTime, getNumberValue } from '@/utils/util'
import { getProjectSelect } from '@api/api'
import ImportItemModal from '@views/bill/dialog/ImportItemModal.vue'

export default {
  name: 'ItemInModal',
  mixins: [JEditableTableMixin, FinancialModalMixin],
  components: {
    ImportItemModal,
    AccountModal,
    PersonModal,
    WorkflowIframe,
    InOutItemModal,
    JUpload,
    JDate,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  data() {
    return {
      title: '操作',
      width: '1600px',
      moreStatus: false,
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      visible: false,
      prefixNo: 'SR',
      model: {},
      fileList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      refKeys: ['accountDataTable'],
      activeKey: 'accountDataTable',
      accountTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '服务名称', key: 'serviceName', width: '20%', type: FormTypes.input, placeholder: '',
            validateRules: [{ required: true, message: '${title}不能为空' }]
          },
          {
            title: '税率', key: 'taxRate', width: '10%', type: FormTypes.inputNumber,
            placeholder: '',
            validateRules: [{ required: true, message: '${title}不能为空' }]
          },
          {
            title: '含税金额（元）', statistics: true, key: 'taxIncludedPrice', width: '10%', type: FormTypes.inputNumber,
            placeholder: '',
            validateRules: [{ required: true, message: '${title}不能为空' }]
          },
          {
            title: '不含税金额', key: 'taxExcludedPrice', width: '10%', type: FormTypes.inputNumber,
            placeholder: ''
          },
          { title: '备注', key: 'remark', width: '20%', type: FormTypes.input, placeholder: '' }
        ]
      },
      confirmLoading: false,
      validatorRules: {
        projectId: {
          rules: [
            { required: true, message: '请选择项目!' }
          ]
        },
        organId: {
          rules: [
            { required: true, message: '请选择客户!' }
          ]
        },
        billTime: {
          rules: [
            { required: true, message: '请选择单据日期!' }
          ]
        },
        accountId: {
          rules: [
            { required: true, message: '请选择收入账户!' }
          ]
        },
        changeAmount: {
          rules: [
            { required: true, message: '请输入收入金额!' }
          ]
        }
      },
      url: {
        add: '/api/invoiceRecord/insert',
        edit: '/api/invoiceRecord/update'
      }
    }
  },
  created() {
  },
  methods: {
    onDeleted(record) {
      this.$nextTick(() => {
        const data = this.accountTable.dataSource.filter(x => ![...record].includes(`${x.id}`))
        console.log(JSON.stringify(data), record, JSON.stringify(this.accountTable.dataSource))
        const taxAmount = data.reduce((acc, cur) => acc + (cur.taxIncludedPrice - 0), 0)
        this.form.setFieldsValue({ 'taxAmount': taxAmount.toFixed(2) })
      })

    },
    onValueChange(event) {
      if (event.column.key === 'taxIncludedPrice') {
        this.form.setFieldsValue({ 'taxAmount': event.target.statisticsColumns.taxIncludedPrice })
      }
    },
    addInit(amountNum) {
      // getAction('/sequence/buildNumber').then((res) => {
      //   if (res && res.code === 200) {
      //     this.form.setFieldsValue({ 'invoiceNumber': amountNum + res.data.defaultNumber })
      //   }
      // })
      this.$nextTick(() => {
        this.form.setFieldsValue({ 'invoiceDate': getNowFormatDateTime(), 'taxAmount': 0 })
      })
    },
    //调用完edit()方法之后会自动调用此方法
    async editAfter() {
      this.billStatus = '0'
      if (this.action === 'add') {
        this.addInit('KP')
        this.fileList = []
      } else {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            ...this.model,
            projectId: this.model.projectId ? this.model.projectId + '' : null
          })
          this.accountTable.dataSource = this.model.items
        })
        this.fileList = this.model.fileName
      }
      this.initSystemConfig()
      this.initOrgan()
      this.initPerson()
      getProjectSelect('hasCode').then(list => this.inOutList = list)
      this.initAccount().then(() => {
        console.log('initAccount', this.accountList)
        this.form.setFieldsValue({
          accountId: this.accountList[0].id
        })
      })
      this.initQuickBtn()
    },
    //提交单据时整理成formData
    classifyIntoFormData(allValues) {
      let billMain = Object.assign(this.model, allValues.formValue)
      let detailArr = allValues.tablesValue[0].values.map(x => (
        {
          ...x,
          id: null
        }
      ))

      if (this.fileList && this.fileList.length > 0) {
        billMain.fileName = this.fileList
      }
      if (this.model.id) {
        billMain.id = this.model.id
      }
      billMain.status = this.billStatus
      return {
        ...billMain,
        items: detailArr
      }
    },
    //改变本次欠款的值
    autoChangeAmount(target) {
      let allEachAmount = target.statisticsColumns.eachAmount - 0
      this.$nextTick(() => {
        this.form.setFieldsValue({ 'changeAmount': allEachAmount })
      })
    },
    onImport() {
      this.$refs.importItemModalForm.add('invoice')
    },
    importItemModalFormOk(data) {
      for (const item of data) {
        if (item.taxRate && typeof item.taxRate === 'string') {
          item.taxRate = item.taxRate.replace('%', '')
        }
        item.taxRate = getNumberValue(item.taxRate)
        item.taxExcludedPrice = getNumberValue(item.taxExcludedPrice)
        item.taxIncludedPrice = getNumberValue(item.taxIncludedPrice)
      }
      this.accountTable.dataSource = data
      const taxAmount = data.reduce((acc, cur) => acc + (cur.taxIncludedPrice - 0), 0)
      this.form.setFieldsValue({ 'taxAmount': taxAmount.toFixed(2) })
    }
  }
}
</script>
<style scoped></style>