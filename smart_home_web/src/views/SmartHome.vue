<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="grid-content bg-purple">
          <temperature :ctemperature="this.Temperature"></temperature>
        </div>
      </el-col>
      <el-col :span="6" :offset="1">
        <div class="grid-content bg-purple">
          <humidity :chumidity="this.Humidity"></humidity>
        </div>
      </el-col>
      <el-col :span="6" :offset="1">
        <div class="grid-content bg-purple">
          <ambientBrightness :cambientBrightness="this.AmbientBrightness"></ambientBrightness>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-switch
              v-model="this.VehACSwitch"
              @change="setVehicleACProperty"
              active-text="空调开"
              inactive-text="空调关">
          </el-switch>
        </div>
      </el-col>
      <el-col :span="6" :offset="1">
        <div class="grid-content bg-purple">
          <el-switch
              v-model="this.Humidified"
              @change="setHumidifierProperty"
              active-text="加湿器开"
              inactive-text="加湿器关">
          </el-switch>
        </div>
      </el-col>
      <el-col :span="6" :offset="1">
        <div class="grid-content bg-purple">
          <el-switch
              v-model="this.LightSwitch"
              @change="setLightProperty"
              active-text="主灯开"
              inactive-text="主灯关">
          </el-switch>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="7" :offset="1">
        <h2>设置自动开空调</h2>
        <div style="text-align: left">
          <el-form ref="form" :model="temperatureForm" label-width="80px" size="small">
            <el-form-item>
              <el-input :disabled="this.autoTemperature" v-model="temperatureForm.minTemperature">
                <template slot="prepend">低于此温度自动打开空调</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input :disabled="this.autoTemperature" v-model="temperatureForm.maxTemperature">
                <template slot="prepend">高于此温度自动打开空调</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="grid-content bg-purple">
                <el-switch
                    v-model="this.autoTemperature"
                    @change="onSubmitTemperatureForm"
                    active-text="开启"
                    inactive-text="关闭">
                </el-switch>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <el-col :span="7">
        <h2>设置自动打开加湿器</h2>
        <div style="text-align: left">
          <el-form ref="form" :model="humidityForm" label-width="80px" size="small">
            <el-form-item>
              <el-input :disabled="this.autoHumidity" v-model="humidityForm.openHumidity">
                <template slot="prepend">低于此湿度自动打开加湿器</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input :disabled="this.autoHumidity" v-model="humidityForm.closeHumidity">
                <template slot="prepend">高于此湿度自动关闭加湿器</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="grid-content bg-purple">
                <el-switch
                    v-model="this.autoHumidity"
                    @change="onSubmitHumidityForm"
                    active-text="开启"
                    inactive-text="关闭">
                </el-switch>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <el-col :span="7">
        <h2>设置自动开灯</h2>
        <div style="text-align: left">
          <el-form ref="form" :model="brightnessForm" label-width="80px" size="small">
            <el-form-item>
              <el-input :disabled="this.autoBrightness" v-model="brightnessForm.closeBrightness">
                <template slot="prepend">高于此亮度自动关灯</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input :disabled="this.autoBrightness" v-model="brightnessForm.openBrightness">
                <template slot="prepend">低于此亮度自动开灯</template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="grid-content bg-purple">
                <el-switch
                    v-model="this.autoBrightness"
                    @change="onSubmitBrightnessForm"
                    active-text="开启"
                    inactive-text="关闭">
                </el-switch>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-col>


    </el-row>
  </div>
</template>

<script>
import temperature from "@/components/Temperature.vue";
import humidity from "@/components/Humidity.vue";
import ambientBrightness from "@/components/AmbientBrightness.vue";
import {
  getAllProperties,
  getAttributeThreshold,
  setAutoBrightness,
  setAutoHumidity,
  setAutoTemperature
} from "@/api/api";
import {setHumidifiedPropertyApi} from "@/api/api";
import {setLightPropertyApi} from "@/api/api";
import {setVehACPropertyApi} from "@/api/api";

export default {
  components: {
    "temperature": temperature,
    "humidity": humidity,
    "ambientBrightness": ambientBrightness
  },
  data() {
    return {
      Humidified: false,
      VehACSwitch: false,
      LightSwitch: false,
      autoTemperature: false,
      autoHumidity: false,
      autoBrightness: false,
      Temperature: 0,
      Humidity: 0,
      AmbientBrightness: 0,
      temperatureForm: {
        autoTemperature: 0,
        minTemperature: 0,
        maxTemperature: 0
      },
      humidityForm: {
        autoHumidity: 0,
        openHumidity: 0,
        closeHumidity: 0
      },
      brightnessForm: {
        autoBrightness: 0,
        openBrightness: 0,
        closeBrightness: 0
      }
    }
  },
  created() {
    this.setSwitch()
    this.updateAllProperties()
    this.init()
  },
  methods: {
    init() {
      getAttributeThreshold().then(response => {
        this.autoBrightness = response.autoBrightness === 1;
        this.autoHumidity = response.autoHumidity === 1;
        this.autoTemperature = response.autoTemperature === 1;
        this.temperatureForm.minTemperature = response.minTemperature;
        this.temperatureForm.maxTemperature = response.maxTemperature;
        this.humidityForm.openHumidity = response.openHumidity;
        this.humidityForm.closeHumidity = response.closeHumidity;
        this.brightnessForm.openBrightness = response.openBrightness;
        this.brightnessForm.closeBrightness = response.closeBrightness;
      }).catch(error => {
        console.log(error)
      })
    },
    onSubmitTemperatureForm(auto) {
      this.autoTemperature = auto;
      this.temperatureForm.autoTemperature = auto ? 1 : 0;
      setAutoTemperature(this.temperatureForm).then(response => {
        console.log(response)
      }).catch(error => {
        console.log(error)
      })
    },
    onSubmitHumidityForm(auto) {
      this.autoHumidity = auto;
      this.humidityForm.autoHumidity = auto ? 1 : 0;
      setAutoHumidity(this.humidityForm).then(response => {
        console.log(response)
      }).catch(error => {
        console.log(error)
      });
    },
    onSubmitBrightnessForm(auto) {
      this.autoBrightness = auto;
      this.brightnessForm.autoBrightness = auto ? 1 : 0;
      setAutoBrightness(this.brightnessForm).then(response => {
        console.log(response)
      }).catch(error => {
        console.log(error)
      });
    },
    setSwitch() {
      getAllProperties().then(response => {
        this.Humidified = response.Humidified === "1";
        this.VehACSwitch = response.VehACSwitch === "1";
        this.LightSwitch = response.LightSwitch === "1";
      }).catch(error => {
        console.log(error)
      })
    },

    //开关
    setVehicleACProperty(property) {
      this.VehACSwitch = property;
      setVehACPropertyApi(property ? 1 : 0)
          .then(response => {
            console.log(response)
          }).catch(error => {
        console.log(error)
      });
    },
    setHumidifierProperty(property) {
      this.Humidified = property;
      setHumidifiedPropertyApi(property ? 1 : 0)
          .then(response => {
            console.log(response)
          }).catch(error => {
        console.log(error)
      });
    },
    setLightProperty(property) {
      this.LightSwitch = property;
      setLightPropertyApi(property ? 1 : 0)
          .then(response => {
            console.log(response)
          }).catch(error => {
        console.log(error)
      });
    },

    updateAllProperties() {
      setInterval(function () {
        getAllProperties().then(response => {
          console.log(response)
          this.Humidified = response.Humidified === "1";
          this.VehACSwitch = response.VehACSwitch === "1";
          this.LightSwitch = response.LightSwitch === "1";
          this.Temperature = response.Temperature;
          this.Humidity = response.Humidity;
          this.AmbientBrightness = response.AmbientBrightness;
        }).catch(error => {
          console.log(error)
        })
      }.bind(this), 5000)

    },
  }
}

</script>

<style scoped lang="scss">

</style>
