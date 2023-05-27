<template>
  <div id="temperature" style="width: 600px; height: 400px" onclick="window.location.href='historicalDate';return false">

  </div>
</template>

<script>
import {getAllProperties} from "@/api/api";

export default {
  props: ['ctemperature'],
  data() {
    return {
      temperature: 0
    }
  },
  watch: {
    ctemperature: function (newVal) {
      console.log(newVal)
      this.temperature = newVal;
    }
  },
  mounted() {
    this.drawChart();
  },
  created() {
    this.setTemperature()
  },
  methods: {

    setTemperature() {
      getAllProperties().then(response => {
        this.temperature = response.Temperature;
      }).catch(error => {
        console.log(error)
      })
    },

    drawChart() {
      var chartDom = document.getElementById('temperature');
      var myChart = this.$echarts.init(chartDom);
      var option;

      option = {
        series: [
          {
            type: 'gauge',
            center: ['50%', '60%'],
            startAngle: 200,
            endAngle: -20,
            min: 0,
            max: 60,
            splitNumber: 12,
            itemStyle: {
              color: '#FFAB91'
            },
            progress: {
              show: true,
              width: 30
            },
            pointer: {
              show: false
            },
            axisLine: {
              lineStyle: {
                width: 30
              }
            },
            axisTick: {
              distance: -45,
              splitNumber: 5,
              lineStyle: {
                width: 2,
                color: '#999'
              }
            },
            splitLine: {
              distance: -52,
              length: 14,
              lineStyle: {
                width: 3,
                color: '#999'
              }
            },
            axisLabel: {
              distance: -20,
              color: '#999',
              fontSize: 20
            },
            anchor: {
              show: false
            },
            title: {
              show: false
            },
            detail: {
              valueAnimation: true,
              width: '60%',
              lineHeight: 40,
              borderRadius: 8,
              offsetCenter: [0, '-15%'],
              fontSize: 40,
              fontWeight: 'bolder',
              formatter: '{value} °C',
              color: 'inherit'
            },
            data: [
              {
                value: this.temperature,
                name: '环境温度'
              }
            ]
          },
          {
            type: 'gauge',
            center: ['50%', '60%'],
            startAngle: 200,
            endAngle: -20,
            min: 0,
            max: 60,
            itemStyle: {
              color: '#FD7347'
            },
            progress: {
              show: true,
              width: 8
            },
            pointer: {
              show: false
            },
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            splitLine: {
              show: false
            },
            axisLabel: {
              show: false
            },
            detail: {
              show: false
            },
            data: [
              {
                value: this.temperature,
                name: '环境温度'
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
                    value: this.temperature,
                    name: '环境温度'
                  }
                ]
              },
              {
                data: [
                  {
                    value: this.temperature,
                    name: '环境温度'
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
