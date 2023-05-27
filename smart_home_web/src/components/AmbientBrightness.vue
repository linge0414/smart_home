<template>
  <div id="main" style="width: 600px; height: 400px" onclick="window.location.href='historicalDate';return false">

  </div>
</template>

<script>
import {getAllProperties} from "@/api/api";

export default {
  props: ['cambientBrightness'],
  data() {
    return {
      ambientBrightness: 0
    }
  },
  watch: {
    cambientBrightness: function (newVal) {
      console.log(newVal)
      this.ambientBrightness = newVal;
    }
  },
  mounted() {
    this.drawChart();
  },
  created() {
    this.setAmbientBrightness()
  },

  methods: {
    setAmbientBrightness() {
      getAllProperties().then(response => {
        this.ambientBrightness = response.AmbientBrightness;
      }).catch(error => {
        console.log(error)
      })
    },

    drawChart() {
      var chartDom = document.getElementById('main');
      var myChart = this.$echarts.init(chartDom);
      var option;

      option = {
        tooltip: {
          formatter: '{a} <br/>{b} : {c}%'
        },
        series: [
          {
            name: '环境亮度',
            type: 'gauge',
            progress: {
              show: true
            },
            detail: {
              valueAnimation: true,
              formatter: '{value}'
            },
            data: [
              {
                value: 0,
                name: '环境亮度'
              }
            ]
          }
        ]
      };
      setInterval(function () {
        myChart.setOption({
          series: [
            {
              data: [
                {
                  value: this.ambientBrightness,
                  name: '环境亮度'
                }
              ]
            },
            {
              data: [
                {
                  value: this.ambientBrightness,
                  name: '环境亮度'
                }
              ]
            }
          ]
        });
      }.bind(this), 2000);
      option && myChart.setOption(option);
    }
  }
}

</script>

<style scoped lang="scss">

</style>
