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
          <a-timeline-item :color="item.type === '收入' ? 'green' : 'red'" v-for="(item, index) in list" :key="index">
            <p>时间：{{ format(item.remark) }}</p>
            <p>
              {{ item.type }}单号：<a @click="showDetail(item)">{{ item.code }}</a>
            </p>
            <p>变动金额：{{ item.totalInAccount }}</p>
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

export default {
  name: 'InOutFlowModal',
  mixins: [mixinDevice],
  data() {
    return {
      title: '项目情况',
      visible: false,
      list: [],
      confirmLoading: false,
    }
  },
  created() {},
  methods: {
    format(val) {
      return dayjs(val).format('YYYY-MM-DD HH:mm:ss')
    },
    edit(record) {
      this.visible = true
      this.confirmLoading = true
      projectFlow({ id: record.id }).then((res) => {
        this.list = res.data
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
      if (item.type === '收入') {
        window.localStorage.setItem('flowItem', JSON.stringify(item))
        link = this.$router.resolve(`/financial/item_in`)
      } else if(item.type === '支出') {
        window.localStorage.setItem('flowItem', JSON.stringify(item))
        link = this.$router.resolve(`/financial/item_out`)
      }

      window.open(link.href, '_target')
    },
  },
}
</script>
<style scoped>
</style>