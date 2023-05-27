<template>
  <div id="humidity" style="width: 600px; height: 400px" onclick="window.location.href='historicalDate';return false">
  </div>
</template>

<script>

import {getAllProperties} from "@/api/api";

export default {
  props: ['chumidity'],
  data() {
    return {
      humidity: 0
    }
  },
  watch: {
    chumidity: function (newVal) {
      console.log(newVal)
      this.humidity = newVal;
    }
  },
  mounted() {
    this.drawChart();
  },
  created() {
    this.setHumidity()
  },
  methods: {
    setHumidity() {
      getAllProperties().then(response => {
        this.humidity = response.Humidity;
      }).catch(error => {
        console.log(error)
      })
    },

    drawChart() {
      var chartDom = document.getElementById('humidity');
      var myChart = this.$echarts.init(chartDom);
      var option;

      option = {
        series: [
          {
            type: 'gauge',
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '75%'],
            radius: '90%',
            min: 0,
            max: 1,
            splitNumber: 8,
            axisLine: {
              lineStyle: {
                width: 6,
                color: [
                  [0.3, '#FF6E76'],
                  [0.8, '#7CFFB2'],
                  [1, '#58D9F9'],
                ]
              }
            },
            pointer: {
              icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
              length: '12%',
              width: 20,
              offsetCenter: [0, '-60%'],
              itemStyle: {
                color: 'inherit'
              }
            },
            axisTick: {
              length: 12,
              lineStyle: {
                color: 'inherit',
                width: 2
              }
            },
            splitLine: {
              length: 20,
              lineStyle: {
                color: 'inherit',
                width: 5
              }
            },
            axisLabel: {
              color: '#464646',
              fontSize: 20,
              distance: -60,
              rotate: 'tangential',
              formatter: function (value) {
                if (value === 0.875) {
                  return '潮湿';
                } else if (value === 0.5) {
                  return '良好';
                } else if (value === 0.125) {
                  return '干燥';
                }
                return '';
              }
            },
            title: {
              offsetCenter: [0, '-10%'],
              fontSize: 20
            },
            detail: {
              fontSize: 30,
              offsetCenter: [0, '-35%'],
              valueAnimation: true,
              formatter: function (value) {
                return Math.round(value * 100) + '';
              },
              color: 'inherit'
            },
            data: [
              {
                value: 0,
                name: '环境湿度'
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
                  value: this.humidity/100,
                  name: '环境湿度'
                }
              ]
            },
            {
              data: [
                {
                  value: this.humidity/100,
                  name: '环境湿度'
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
