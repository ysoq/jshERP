<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
      :visible="visible"
      :confirmLoading="confirmLoading"
      :getContainer="() => $refs.container"
      :maskStyle="{ top: '93px', left: '154px' }"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="取消"
      okText="保存"
      style="top: 10%; height: 80%"
    >
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel"> 取消 </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form" id="inOutItemModal">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编号">
            <a-input placeholder="编号" v-decorator.trim="['code', validatorRules.code]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
            <a-input placeholder="请输入名称" v-decorator.trim="['name', validatorRules.name]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目经理">
            <a-select placeholder="选择项目经理" v-decorator="['manager',validatorRules.manager]" :dropdownMatchSelectWidth="false">
              <a-select-option v-for="(item, index) in userList" :key="index" :value="item.id">
                {{ item.userName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同金额">
            <a-input-number
              style="width: 100%"
              placeholder="请输入合同金额"
              v-decorator.trim="['contractPrice', validatorRules.contractPrice]"
              :min="0"
              :max="999999999"
            ></a-input-number>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
            <a-input placeholder="请输入排序" v-decorator.trim="['sort']" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
            <a-textarea :rows="2" placeholder="请输入备注" v-decorator="['remark']" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="文件">
            <j-upload v-model="fileList" bizPath="bill"></j-upload>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
import pick from 'lodash.pick'
import { addInOutItem, editInOutItem, checkInOutItem, getUserList } from '@/api/api'
import { autoJumpNextInput } from '@/utils/util'
import { mixinDevice } from '@/utils/mixin'
  import JUpload from '@/components/jeecg/JUpload'
  export default {
  name: 'InOutItemModal',
  mixins: [mixinDevice],
  components: { JUpload },
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      typeParam: '',
      userList: [],
      isReadOnly: false,
      fileList: '',
      typeDisabled: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        name: {
          rules: [
            { required: true, message: '请输入名称!' },
            { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' },
          ],
        },
        type: {
          rules: [{ required: true, message: '请选择类型!' }],
        },
        manager: {
          rules: [{ required: true, message: '请选择项目经理!' }],
        },
        code: {
          rules: [{ required: true, message: '请输入编号!' }],
        },
      },
    }
  },
  created() {
    this.initUser()
  },
  methods: {
    initUser() {
      getUserList({}).then((res) => {
        if (res) {
          this.userList = res
        }
      })
    },
    add(type) {
      this.typeParam = type
      this.edit({})
      this.fileList = ''
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      if (this.typeParam) {
        this.typeDisabled = true
        if (this.typeParam === 'in') {
          this.model.type = '收入'
        } else if (this.typeParam === 'out') {
          this.model.type = '支出'
        }
      } else {
        this.typeDisabled = false
      }
      this.visible = true
      this.fileList = this.model.fileList

      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(this.model, 'name', 'code', 'contractPrice', 'fileList', 'manager', 'sort', 'remark')
        )
        autoJumpNextInput('inOutItemModal')
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
          let formData = Object.assign(this.model, values)
          formData.fileList = that.fileList

          let obj
          if (!this.model.id) {
            obj = addInOutItem(formData)
          } else {
            obj = editInOutItem(formData)
          }
          obj
            .then((res) => {
              if (res.code === 200) {
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.data.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    },
    handleCancel() {
      this.close()
    },
    validatePersonName(name, code) {
      let params = {
        name: name,
        code: code,
        id: this.model.id ? this.model.id : 0,
      }
      return checkInOutItem(params).then((res) => {
        if (res && res.code === 200) {
          if (!res.data.status) {
            return Promise.resolve()
          } else {
            return Promise.reject(res.data)
          }
        } else {
          return Promise.reject({ message: '验证失败' })
        }
      })
    },
  },
}
</script>
<style scoped>
</style>