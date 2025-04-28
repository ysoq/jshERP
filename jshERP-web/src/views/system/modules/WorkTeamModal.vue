<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
      :visible="visible"
      :confirmLoading="confirmLoading"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="取消"
      okText="保存"
      style="top:10%;height: 70%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          取消
        </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form" id="workTeamModal">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="班组名称">
            <a-input placeholder="" v-decorator.trim="[ 'teamName', validatorRules.teamName]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="联系人">
            <a-input placeholder="" v-decorator.trim="[ 'contactPerson' ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="联系电话">
            <a-input placeholder="" v-decorator.trim="[ 'phone' ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
            <a-textarea :rows="2" placeholder="请输入备注" v-decorator.trim="[ 'remark' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
import pick from 'lodash.pick'
import { addDepot, editDepot, checkDepot, getUserList } from '@/api/api'
import { autoJumpNextInput } from '@/utils/util'
import { mixinDevice } from '@/utils/mixin'
import { postAction, putAction } from '@api/manage'

export default {
  name: 'WorkTeamModal',
  mixins: [mixinDevice],
  data () {
    return {
      title: '操作',
      visible: false,
      model: {},
      maskStyle: '',
      userList: [],
      isReadOnly: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        teamName: {
          rules: [
            { required: true, message: '请输入班组名称' },
            { validator: this.validateDepotName }
          ]
        }
      }
    }
  },

  methods: {
    add () {
      this.edit({})
    },
    edit (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model,
          'teamName', 'contactPerson', 'phone', 'remark'))
        autoJumpNextInput('workTeamModal')
      })
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    handleOk () {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(this.model, values)
          let obj
          if (!this.model.id) {
            obj = postAction('/api/workTeam/insert', formData)
          } else {
            obj = putAction('/api/workTeam/update', formData)
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
    handleCancel () {
      this.close()
    }
  }
}
</script>
<style scoped>

</style>