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
        <a-button key="back" v-if="disableSubmit" @click="handleCancel"> 取消</a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form" id="inOutItemModal">

          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编号" v-if="model.projectIds">
            <a-input placeholder="编号" v-decorator.trim="['code',  validatorRules.code  ]"
                     :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编号" v-else>
            <a-input placeholder="编号" v-decorator.trim="['code']"
                     :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
            <a-input placeholder="请输入名称" v-decorator.trim="['name', validatorRules.name]"
                     :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型">
            <a-select placeholder="请选择类型" v-decorator="[ 'type', validatorRules.type]" :disabled="disableSubmit">
              <a-select-option value="大包">大包</a-select-option>
              <a-select-option value="清包">清包</a-select-option>
              <a-select-option value="运维">运维</a-select-option>
            </a-select>
          </a-form-item>


          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="班组">
            <a-select placeholder="选择班组" v-decorator="['teamList']"
                      optionFilterProp="children" mode="multiple"
                      :dropdownMatchSelectWidth="false" showSearch :disabled="disableSubmit">
              <a-select-option v-for="(item, index) in workTeamList" :key="index" :value="`${item.id}`">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>


          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目经理">
            <a-select placeholder="选择项目经理" v-decorator="['manager',validatorRules.manager]"
                      optionFilterProp="children"
                      :dropdownMatchSelectWidth="false" showSearch :disabled="disableSubmit">
              <a-select-option v-for="(item, index) in userList" :key="index" :value="item.id">
                {{ item.userName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
            <a-select placeholder="选择客户" v-decorator="['supplierId']" showSearch optionFilterProp="children"
                      :dropdownMatchSelectWidth="false" :disabled="disableSubmit">
              <a-select-option v-for="(item, index) in supplierList" :key="index" :value="item.id">
                {{ item.supplier }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同金额">
            <a-input-number
              style="width: 100%" :disabled="disableSubmit"
              placeholder="请输入合同金额"
              v-decorator.trim="['contractPrice', validatorRules.contractPrice]"
              :min="0"
              :max="999999999"
            ></a-input-number>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目完成时间">
            <j-date v-decorator="['finishTime']" dateFormat="YYYY-MM-DD" :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
            <a-input placeholder="请输入排序" v-decorator.trim="['sort']" :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
            <a-textarea :rows="2" placeholder="请输入备注" v-decorator="['remark']" :disabled="disableSubmit" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同">
            <j-upload v-decorator="['file1']" bizPath="bill" :disabled="disableSubmit"></j-upload>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开工报告">
            <j-upload v-decorator="['file2']" bizPath="bill" :disabled="disableSubmit"></j-upload>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="验收资料">
            <j-upload v-decorator="['file3']" bizPath="bill" :disabled="disableSubmit"></j-upload>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="送审资料">
            <j-upload v-decorator="['file4']" bizPath="bill" :disabled="disableSubmit"></j-upload>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="审价报告">
            <j-upload v-decorator="['file5']" bizPath="bill" :disabled="disableSubmit"></j-upload>
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
import { getAction, postAction } from '@api/manage'
import JDate from '@comp/jeecg/JDate.vue'
import { mapGetters } from 'vuex'

export default {
  name: 'InOutItemModal',
  mixins: [mixinDevice],
  components: { JDate, JUpload },
  props: {
    workTeamList: Array
  },
  data () {
    return {
      title: '操作',
      visible: false,
      disableSubmit: false,
      model: {},
      typeParam: '',
      userList: [],
      supplierList: [],
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
        name: {
          rules: [
            { required: true, message: '请输入名称!' },
          ]
        },
        type: {
          rules: [{ required: true, message: '请选择类型!' }]
        },
        manager: {
          rules: [{ required: true, message: '请选择项目经理!' }]
        },
        code: {
          rules: [{ required: true, message: '请输入编号!' }]
        }
      }
    }
  },
  created () {
    this.initUser()
  },
  methods: {
    ...mapGetters(['nickname']),
    initUser () {
      const args = {
        search: `{"supplier":"","type":"客户","telephone":"","phonenum":""}`,
        currentPage: 1,
        pageSize: 1000,
        column: 'createTime',
        order: 'desc'
      }
      getAction('/supplier/list', args).then(res => {
        this.supplierList = res.data.rows
      })

      getUserList({}).then((res) => {
        if (res) {
          this.userList = res
        }
      })

    },
    add (defaultData) {
      this.edit(Object.assign({ projectIds: null }, defaultData))
    },
    edit (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(this.model, 'name', 'code', 'type', 'contractPrice', 'supplierId', 'manager', 'finishTime', 'sort', 'remark')
        )

        const fileList = JSON.parse(this.model.fileList || '{}')
        this.form.setFieldsValue({ 'file1': fileList.file1 })
        this.form.setFieldsValue({ 'file2': fileList.file2 })
        this.form.setFieldsValue({ 'file3': fileList.file3 })
        this.form.setFieldsValue({ 'file4': fileList.file4 })
        this.form.setFieldsValue({ 'file5': fileList.file5 })
        const teamList = this.model.teamList ? this.model.teamList.split(',') : []
        this.form.setFieldsValue({ 'teamList': teamList })

        autoJumpNextInput('inOutItemModal')
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
          delete formData.file1
          delete formData.file2
          delete formData.file3
          delete formData.file4
          delete formData.file5

          formData.fileList = JSON.stringify({
            file1: values.file1,
            file2: values.file2,
            file3: values.file3,
            file4: values.file4,
            file5: values.file5
          })
          formData.teamList = values.teamList.join(',')

          let editType = 'insert'
          let obj
          if (!this.model.id) {
            obj = addInOutItem(formData)
          } else {
            editType = 'update'
            obj = editInOutItem(formData)
          }
          obj
            .then((res) => {
              if (res.code === 200) {
                that.$emit('ok', editType)
                that.addMergeMsg(formData.name)
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
    addMergeMsg (projectName) {
      if (this.model.projectIds && this.model.projectIds.length > 1) {
        for (const projectId of this.model.projectIds) {
          let msgParam = {
            msgTitle: `由${this.nickname()}填写`,
            msgContent: `合并到【${projectName}】`,
            inOutItemId: projectId,
            projectStatus: '99',
            recoverFile: '',
            type: '项目进度'
          }
          postAction('/msg/add', msgParam)
        }
      }
    },
    handleCancel () {
      this.close()
    },
    validatePersonName (name, code) {
      let params = {
        name: name,
        code: code,
        id: this.model.id ? this.model.id : 0
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
    }
  }
}
</script>
<style scoped>
</style>