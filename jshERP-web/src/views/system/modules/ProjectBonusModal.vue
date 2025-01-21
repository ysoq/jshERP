<template>
  <div ref='container'>
    <a-modal
      :title='title'
      :width='600'
      :visible='visible'
      :confirmLoading='confirmLoading'
      :getContainer='() => $refs.container'
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName='wrapClassNameInfo()'
      :mask='isDesktop()'
      :maskClosable='false'
      @ok='handleOk'
      @cancel='handleCancel'
      cancelText='取消'
      okText='保存'
      style='top:15%;height: 60%;'>
      <template slot='footer'>
        <a-button key='back' v-if='isReadOnly' @click='handleCancel'>
          取消
        </a-button>
      </template>
      <a-spin :spinning='confirmLoading'>
        <a-form :form='form' id='memberModal'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='金额'>
            <a-input-number
              style='width: 100%'
              placeholder='请输入金额'
              v-decorator.trim="['totalPrice', validatorRules.totalPrice]"
              :min='0'
              :max='999999999'
            ></a-input-number>
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='备注'>
            <a-textarea :rows='2' placeholder='请输入备注' v-decorator.trim="[ 'remark' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
import pick from 'lodash.pick'
import { addSupplier, editSupplier, checkSupplier } from '@/api/api'
import { autoJumpNextInput } from '@/utils/util'
import { mixinDevice } from '@/utils/mixin'
import { getAction, httpAction } from '@api/manage'
import dayjs from 'dayjs'

export default {
  name: 'ProjectBonusModal',
  mixins: [mixinDevice],
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      isReadOnly: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 4 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 20 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        totalPrice: {
          rules: [
            { required: true, message: '请输入金额!' }
          ]
        }
      }
    }
  },
  created() {
  },
  methods: {
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if(!this.model.billNo) {
        getAction('/sequence/buildNumber').then((res) => {
          if (res && res.code === 200) {
            this.model.billNo= 'JEFP' + res.data.defaultNumber
          }
        })
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          'totalPrice': this.model.totalPrice ? Math.abs(this.model.totalPrice ) : null,
          'remark': this.model.remark
        })
        autoJumpNextInput('memberModal')
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          const args = {
            info: JSON.stringify({
              'billTime': this.model.billTime || dayjs().format('YYYY-MM-DD'),
              'billNo': this.model.billNo,
              'remark': values.remark,
              'changeAmount': values.totalPrice * -1,
              'type': '分配金额',
              'totalPrice': values.totalPrice * -1,
              'status': '0',
              'id': this.model.id
            }),
            rows: JSON.stringify([
              {
                "eachAmount": values.totalPrice,
                "inOutItemId": this.model.inOutItemId
              }
            ])
          }
          let obj
          if (!this.model.id) {
            obj = httpAction('/accountHead/addAccountHeadAndDetail', args, 'post')
          } else {
            obj = httpAction('/accountHead/updateAccountHeadAndDetail', args, 'put')
          }
          obj.then((res) => {
            if (res.code === 200) {
              that.$emit('ok')
            } else {
              that.$message.warning(res.data.message)
            }
          }).finally(() => {
            that.confirmLoading = false
            that.close()
          })
        }
      })
    },
    handleCancel() {
      this.close()
    },
    validateSupplierName(rule, value, callback) {
      let params = {
        name: value,
        type: '会员',
        id: this.model.id ? this.model.id : 0
      }
      checkSupplier(params).then((res) => {
        if (res && res.code === 200) {
          if (!res.data.status) {
            callback()
          } else {
            callback('会员卡号已经存在')
          }
        } else {
          callback(res.data)
        }
      })
    }
  }
}
</script>
<style scoped>

</style>