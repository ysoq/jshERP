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
      @cancel="handleCancel"
      cancelText="关闭"
      okText="保存"
      :ok-button-props="{ style: { display: 'none' } }"
      style="top: 10%; height: 80%"
    >
      <a-spin :spinning="confirmLoading">
        <a-timeline>
          <a-timeline-item :color="item.color" v-for="(item, index) in list" :key="index">
            <p style="margin-bottom: 5px;color: #555;font-size: 14px;font-weight: 600;">
              时间：{{ format(item.createTime) }} </p>
            <div>
              <template v-if="item.type === '项目进度'">
                <p style="margin-bottom: 3px">{{ item.msgTitle }} <span
                  v-if="item.projectStatus">，项目进度：<a>{{ getProjectStatusText(item.projectStatus) }}</a></span></p>
                <p v-if="item.msgContent">进度：{{ item.msgContent }}</p>
                <div style="display: flex;" v-if="item.recoverFile">附件：
                  <div><p v-for="(f,i) in item.recoverFile" :key="i"><a target="_blank"
                                                                        :href="`/jshERP-boot/systemConfig/static/${f}`">
                    {{ f
                    }}</a></p></div>
                </div>
              </template>
              <template v-else>
                <p style="margin-bottom: 3px">
                  {{ item.subType ? item.subType.replace('销售', '材料') + '-' : '' }}{{ item.type }}
                </p>
                <p style="margin-bottom: 3px">
                  单号：<a @click="showDetail(item)">{{ item.number }}</a>
                </p>
                <p>变动金额： <span
                  style="color: orangered">{{ (item.type === '发票' || item.type === '收入' || item.subType === '销售退货' ? 1 : -1) * Math.abs(item.totalPrice)
                  }}</span>元</p>
              </template>
            </div>

          </a-timeline-item>
        </a-timeline>
      </a-spin>

      <template slot="footer">
        <a-button @click='showDetail({type: "收支汇总", inOutItemId: `${record.id}`})'>
          收支汇总
        </a-button>
        <a-button @click='showDetail({type: "领料汇总", inOutItemId: `${record.id}`})'>
          领料汇总
        </a-button>
        <a-button type="primary" @click="handleCancel">
          关闭
        </a-button>
      </template>
    </a-modal>
  </div>
</template>
<script>
import { projectFlow } from '@/api/api'
import { mixinDevice } from '@/utils/mixin'
import dayjs from 'dayjs'
import { getProjectStatusText } from '@views/system/InOutItemCommon'

export default {
  name: 'InOutFlowModal',
  mixins: [mixinDevice],
  data () {
    return {
      title: '项目情况',
      visible: false,
      list: [],
      confirmLoading: false,
      record: null
    }
  },
  created () {
  },
  methods: {
    getProjectStatusText,
    format (val) {
      return dayjs(val).format('YYYY-MM-DD HH:mm:ss')
    },
    edit (record) {
      this.visible = true
      this.confirmLoading = true
      this.record = record
      projectFlow({ id: record.id }).then((res) => {
        const list = res.data
          .map(x => ({
            ...x,
            color: x.type === '收入' || x.type === '发票' ? 'green' : 'red'
          }))
          .concat(record.msgList.map(x => ({
            ...x,
            recoverFile: x.recoverFile ? x.recoverFile.split(',') : null,
            type: '项目进度',
            color: x.projectStatus === '2' ? 'blue' : '#777'
          })))

        this.list = list.sort((a, b) => dayjs(a.createTime).isBefore(b.createTime) ? 1 : -1)
        this.confirmLoading = false
      })
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    handleCancel () {
      this.close()
    },
    showDetail (item) {
      let link = ''
      window.localStorage.setItem('flowItem', JSON.stringify(item))
      if (item.type === '收支汇总') {
        link = this.$router.resolve(`/report/income_expend`)
      } else if (item.type === '领料汇总') {
        link = this.$router.resolve(`/report/in_out_detail`)
      } else if (item.type === '收入') {
        link = this.$router.resolve(`/financial/item_in`)
      } else if (item.type === '支出') {
        link = this.$router.resolve(`/financial/item_out`)
      } else if (item.type === '发票') {
        link = this.$router.resolve(`/financial/invoice_record`)
      } else if (item.code.startsWith('QTCK')) {
        link = this.$router.resolve(`/bill/other_out`)
      } else if (item.code.startsWith('XSCK')) {
        link = this.$router.resolve(`/bill/sale_out`)
      }
      window.open(link.href, '_target')
    }
  }
}
</script>
<style scoped>
</style>