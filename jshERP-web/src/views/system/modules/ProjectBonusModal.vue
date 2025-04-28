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
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='项目'>
            <a-select placeholder="" v-decorator.trim="[ 'projectId', validatorRules.projectId ]" optionFilterProp="children"
                      :dropdownMatchSelectWidth="false" showSearch allowClear>
              <a-select-option v-for="(item, index) in bonusList" :key="index" :value="item.id">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='班组'>
            <a-select placeholder="" v-decorator.trim="[ 'teamId', validatorRules.teamId ]" optionFilterProp="children"
                      :dropdownMatchSelectWidth="false" showSearch allowClear>
              <a-select-option v-for="(item, index) in workTeamList" :key="index" :value="item.id">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='金额'>
            <a-input-number
              style='width: 100%'
              placeholder='请输入金额'
              v-decorator.trim="['amount', validatorRules.amount]"
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
  props: {
    workTeamList: Array,
    bonusList: Array,
  },
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
        projectId: {
          rules: [
            { required: true, message: '请选择项目!' }
          ]
        },
        teamId: {
          rules: [
            { required: true, message: '请选择班组!' }
          ]
        },
        amount: {
          rules: [
            { required: true, message: '请输入金额!' }
          ]
        },
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

      this.$nextTick(() => {
        this.form.setFieldsValue({
          'remark': this.model.remark,
          'projectId': this.model.projectId,
          'teamId': this.model.teamId,
          'amount': this.model.amount,
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
             ...values,
            'id': this.model.id ? this.model.id : null,
          }
          let obj
          if (!this.model.id) {
            obj = httpAction('/api/projectAmount/insert', args, 'post')
          } else {
            obj = httpAction('/api/projectAmount/update', args, 'put')
          }
          obj.then((res) => {
            if (res.code === 200) {
              that.$emit('ok')
              that.close()
            } else {
              that.$message.warning(res.data.message)
            }
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    handleCancel() {
      this.close()
    },
  }
}
</script>
<style scoped>

</style>