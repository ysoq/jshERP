<template>
  <div style="width: 100%;height: 100%">
    <div class='img-bg'>
      <img src='/static/logo-1.png' alt=''/>
    </div>
  </div>
</template>

<script>
  import IndexChart from './IndexChart'

  export default {
    name: "Analysis",
    components: {
      IndexChart
    },
    data() {
      return {

      }
    },
    created() {

    },
    methods: {

    }
  }
</script>
<style scoped>
.img-bg  {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  margin-top: 100px;
}
img {
  width: 500px;
}
</style>