<template>
  <div ref='container'>
    <a-modal
      :title='title'
      :width='800'
      :visible='visible'
      :confirmLoading='confirmLoading'
      :getContainer='() => $refs.container'
      :maskStyle="{ top: '93px', left: '154px' }"
      :wrapClassName='wrapClassNameInfo()'
      :mask='isDesktop()'
      :maskClosable='false'
      @ok='handleOk'
      @cancel='handleCancel'
      cancelText='取消'
      okText='保存'
      style='top: 10%; height: 80%'
    >
      <template slot='footer'>
        <a-button key='back' v-if='isReadOnly' @click='handleCancel'> 取消</a-button>
      </template>
      <a-spin :spinning='confirmLoading'>
        <a-form :form='form' id='inOutItemModal'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='项目经理'>
            <span>{{ model.username }}</span>
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='项目状态'>
            <a-radio-group button-style='solid' v-model='model.projectStatus'>
              <a-radio-button value='1'>
                进行中
              </a-radio-button>
              <a-radio-button value='2'>
                已完成
              </a-radio-button>
            </a-radio-group>
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='正文'>
            <a-textarea :rows='8' placeholder='正文' initialValue=''
                        v-decorator.trim="['msgContent', validatorRules.msgContent]" />
          </a-form-item>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='附件'>
            <j-upload v-decorator="['recoverFile']" bizPath='bill'></j-upload>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
import pick from 'lodash.pick'
import { autoJumpNextInput } from '@/utils/util'
import { mixinDevice } from '@/utils/mixin'
import JUpload from '@/components/jeecg/JUpload'
import { postAction } from '@api/manage'
import { mapGetters } from 'vuex'

export default {
  name: 'InOutItemModal',
  mixins: [mixinDevice],
  components: { JUpload },
  data() {
    return {
      title: '操作',
      visible: false,
      editMode: false,
      model: {},
      typeParam: '',
      userList: [],
      isReadOnly: false,
      fileList: '',
      typeDisabled: false,
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
        msgTitle: {
          rules: [
            { required: true, message: '请输入标题!' },
            { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
          ]
        },
        msgContent: {
          rules: [
            { required: true, message: '请请输入内容!' },
            { min: 1, max: 250, message: '长度在 1 到 2500 个字符', trigger: 'blur' }
          ]
        },
        recoverContent: {
          rules: [
            { required: true, message: '请输入回复内容!' },
            { min: 1, max: 250, message: '长度在 1 到 2500 个字符', trigger: 'blur' }
          ]
        }
      }
    }
  },
  created() {
  },
  methods: {
    ...mapGetters(['nickname']),
    add(record) {
      this.title = '进度填写'
      this.edit(record)
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)

      this.visible = true

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
          let formData = Object.assign(this.model, values)
          let msgParam = {
            msgTitle: `由${this.nickname()}填写`,
            msgContent: formData.msgContent,
            inOutItemId: formData.inOutItemId,
            projectStatus: formData.projectStatus,
            recoverFile: formData.recoverFile,
            type: '项目进度'
          }

          that.confirmLoading = true
          postAction('/msg/add', msgParam).then(res => {
            if (res.code === 200) {
              that.$emit('ok')
              that.close()
            } else {
              that.$message.warning(res.data.message)
            }
          }).finally(()=> {
            that.confirmLoading = false
          })
        }
      })
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>
<style scoped>
</style>