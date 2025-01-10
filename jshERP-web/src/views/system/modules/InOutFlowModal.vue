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
      @cancel='handleCancel'
      cancelText='关闭'
      okText='保存'
      :ok-button-props="{ style: { display: 'none' } }"
      style='top: 10%; height: 80%'
    >
      <a-spin :spinning='confirmLoading'>
        <a-timeline>
          <a-timeline-item :color='item.color' v-for='(item, index) in list' :key='index'>
            <p style='margin-bottom: 5px;color: #555;font-size: 16px;font-weight: 600;'>时间：{{ format(item.createTime) }}</p>
            <div style='margin-left: 8px'>
              <template v-if="item.type === '项目进度'">
                <p style='margin-bottom: 3px'>{{ item.msgTitle }} <span v-if='item.projectStatus'>，项目进度：<a>{{getProjectStatusText(item.projectStatus) }}</a></span> </p>
                <p  v-if='item.msgContent'>进度：{{ item.msgContent }}</p>
                <div style='display: flex;' v-if='item.recoverFile'>附件：
                  <div><p v-for='(f,i) in item.recoverFile' :key='i'><a target='_blank'
                                                                        :href='`/jshERP-boot/systemConfig/static/${f}`'> {{ f
                    }}</a></p></div>
                </div>
              </template>
              <template v-else>
                <p style='margin-bottom: 3px'>
                  {{ item.type }}单号：<a @click='showDetail(item)'>{{ item.code }}</a>
                </p>
                <p>变动金额： <span style='color: orangered'>{{ (item.type === '收入' ? 1 : -1) * Math.abs(item.totalInAccount)
                  }}</span>元</p>
              </template>
            </div>

          </a-timeline-item>
        </a-timeline>
      </a-spin>
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
  data() {
    return {
      title: '项目情况',
      visible: false,
      list: [],
      confirmLoading: false
    }
  },
  created() {
  },
  methods: {
    getProjectStatusText,
    format(val) {
      return dayjs(val).format('YYYY-MM-DD HH:mm:ss')
    },
    edit(record) {
      this.visible = true
      this.confirmLoading = true
      projectFlow({ id: record.id }).then((res) => {
        // item.type === '收入' || item.projectStatus === '2' ? 'green' : 'red'
        const list = res.data
          .map(x => ({ ...x,
            createTime: x.remark,
            color: x.type === '收入' ? 'green' : 'red' }))
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
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleCancel() {
      this.close()
    },
    showDetail(item) {
      let link = ''
      window.localStorage.setItem('flowItem', JSON.stringify(item))

      if (item.type === '收入') {
        link = this.$router.resolve(`/financial/item_in`)
      } else if (item.type === '支出') {
        link = this.$router.resolve(`/financial/item_out`)
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