<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :forceRender="true"
    :style="modalStyle"
    fullscreen
    switchFullscreen
    @cancel="handleCancel"
    wrapClassName="ant-modal-cust-warp">
    <template slot="footer">
      <a-button key="back" @click="handleCancel">取消(ESC)</a-button>
      <!--此处为解决缓存问题-->
      <a-button v-if="model" v-print="'#itemInPrint'">打印</a-button>
      <!--反审核-->
      <a-button v-if="isCanBackCheck && model.status==='1'" @click="handleBackCheck()">反审核</a-button>
    </template>
    <a-form :form="form">
      <!--收入-->
      <template>
        <section ref="print" id="itemInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目">
                {{ model.projectId }}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="往来单位">
                {{ model.supplierId }}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{ model.invoiceDate }}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{ model.invoiceNumber }}
              </a-form-item>
            </a-col>
          </a-row>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :pagination="false"
            :loading="loading"
            :columns="itemInColumns"
            :dataSource="dataSource">
          </a-table>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label=""
                           style="padding:20px 10px;">
                {{ model.remark }}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="收入账户">
                {{ model.accountId }}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开票含税金额">
                {{ model.taxAmount }}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
            <a-col :span="6"></a-col>
          </a-row>
        </section>
      </template>

      <template v-if="fileList && fileList.length>0">
        <a-row class="form-row" :gutter="24">
          <a-col :span="12">
            <a-form-item :labelCol="{xs: { span: 24 },sm: { span: 3 }}"
                         :wrapperCol="{xs: { span: 24 },sm: { span: 21 }}" label="附件">
              <j-upload v-model="fileList" bizPath="bill" :disabled="true" :buttonVisible="false"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12"></a-col>
        </a-row>
      </template>
    </a-form>
  </j-modal>
</template>
<script>
import { postAction } from '@/api/manage'
import JUpload from '@/components/jeecg/JUpload'

export default {
  name: 'IncomeDetailModal',
  components: {
    JUpload
  },
  data () {
    return {
      title: '详情',
      width: '1600px',
      visible: false,
      modalStyle: '',
      model: {},
      isCanBackCheck: true,
      fileList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      form: this.$form.createForm(this),
      loading: false,
      dataSource: [],
      url: {
        batchSetStatusUrl: '/api/invoiceRecord/batchSetStatus'
      },
      itemInColumns: [
        {
          title: '#', dataIndex: '', width: '5%', align: 'center', customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        { title: '服务名称', dataIndex: 'serviceName', width: '20%' },
        { title: '税率', dataIndex: 'taxRate', width: '10%' },
        { title: '含税金额（元）', dataIndex: 'taxIncludedPrice', width: '10%' },
        { title: '不含税金额', dataIndex: 'taxExcludedPrice', width: '10%' },
        { title: '备注', dataIndex: 'remark', width: '20%' },
      ]
    }
  },
  created () {
    let realScreenWidth = window.screen.width
    this.width = realScreenWidth < 1500 ? '1200px' : '1550px'
  },
  methods: {
    show (record ) {
      console.log('IncomeDetailModal show', record)
      this.fileList = record.fileName
      this.modalStyle = 'top:20px;height: 95%;display: block;'
      this.model = Object.assign({}, record)
      this.$nextTick(() => {
        this.visible = true
        // this.form.setFieldsValue(pick(this.model, 'id'))
      })
      this.dataSource = record.items
    },

    handleBackCheck () {
      let that = this
      this.$confirm({
        title: '确认操作',
        content: '是否对该单据进行反审核?',
        onOk: function() {
          that.loading = true
          postAction(that.url.batchSetStatusUrl, { status: '0', ids: that.model.id }).then((res) => {
            if (res.code === 200) {
              that.$emit('ok')
              that.loading = false
              that.close()
            } else {
              that.$message.warning(res.data.message)
              that.loading = false
            }
          }).finally(() => {
          })
        }
      })
    },
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.modalStyle = ''
    }
  }
}
</script>

<style scoped>

</style>