<template>
  <div>
    <historical :ctime="this.time" :ctemperature="this.temperature" :chumidity="this.humidity" :cambientBrightness="this.ambientBrightness"></historical>
    <el-button type="primary" round @click="backToHome">返回主页</el-button>
  </div>
</template>

<script>
import HistoricalDateComponent from "@/components/HistoricalDateComponent.vue";
import {getHistoricalData} from "@/api/api";

export default {
  components: {
    "historical": HistoricalDateComponent
  },
  data() {
    return {
      responseData: [],
      temperature: [],
      humidity: [],
      ambientBrightness: [],
      time: [],
    }
  },
  created() {
    this.update();
  },
  methods: {
    backToHome() {
      this.$router.push({path: '/'})
    },
    update() {
      getHistoricalData().then(response => {
        for (let i = 0; i < response.length; i++) {
          this.temperature[i] = response[i].temperature;
          this.humidity.push(response[i].humidity);
          this.ambientBrightness.push(response[i].ambientBrightness);
          this.time.push(new Date(response[i].time));
        }
      }).catch(error => {
        console.log(error)
      })
    },
  }

}
</script>
