<template>
  <div id="main" style="width: 1500px; height: 600px">

  </div>
</template>

<script>
import {getHistoricalData} from "@/api/api";

export default {
  props: ['ctime', 'ctemperature', 'chumidity', 'cambientBrightness'],
  data() {
    return {
      temperature: [],
      humidity: [],
      ambientBrightness: [],
      time: [],
    }
  },
  mounted() {
    setTimeout(()=>{
      //这里就写你要执行的语句即可，先让数据库的数据加载进去数组中你在从数组中取值就好了
      this.drawChart();
    },100)

  },
  created() {
    this.time = this.ctime
    this.temperature = this.ctemperature
    this.humidity = this.chumidity
    this.ambientBrightness = this.cambientBrightness
  },


  methods: {
    drawChart() {
      var chartDom = document.getElementById('main');
      var myChart = this.$echarts.init(chartDom);
      var option;

      var _this = this;

      function getTemperatureData(i) {
        return {
          name: _this.time[i].toString(),
          value: [_this.time[i], _this.temperature[i]],
        }
      }

      function getHumidityData(i) {
        return {
          name: _this.time[i].toString(),
          value: [_this.time[i], _this.humidity[i]],
        }
      }

      function getAmbientBrightnessData(i) {
        return {
          name: _this.time[i].toString(),
          value: [_this.time[i], _this.ambientBrightness[i]],
        }
      }


      let temperatureData = [];
      let humidityData = [];
      let ambientBrightnessData = [];


      for (let i = 0; i < this.time.length; i++) {
        temperatureData.push(getTemperatureData(i));
        humidityData.push(getHumidityData(i));
        ambientBrightnessData.push(getAmbientBrightnessData(i));
      }

      console.log(humidityData);


      option = {
        title: {
          text: '历史监测数据'
        },
        dataZoom: [
          {
            type: 'inside',
            start: 0,
            end: 100
          },
          {
            start: 0,
            end: 100
          }
        ],
        toolbox: {
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            magicType: { type: ['line', 'bar'] },
            dataView: { readOnly: true },
            restore: {},
            saveAsImage: {}
          }
        },
        legend: {
          data: ['温度', '湿度', '亮度']
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            animation: false
          }
        },
        xAxis: {
          type: 'time',
        },
        yAxis: {
          type: 'value',
          boundaryGap: [0, '100%'],
        },
        series: [
          {
            name: '温度',
            type: 'line',
            data: temperatureData
          },
          {
            name: '湿度',
            type: 'line',
            data: humidityData
          },
          {
            name: '亮度',
            type: 'line',
            data: ambientBrightnessData
          },
        ]
      };

      option && myChart.setOption(option);
    },
  },

}
</script>

