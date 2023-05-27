//将拦截器整体导入
import request from '../utils/Interceptor.js'//导入已经写好的拦截器

// 封装所有的API接口

export function getAllProperties(){
    return request({
        url:'/iot/getAllProperties',
        method :'get'
    })
}

export function setVehACPropertyApi(property){
    return request({
        url:`/iot/setVehACProperty/${property}`,
        method :'get'
    })
}

export function setLightPropertyApi(property){
    return request({
        url:`/iot/setLightProperty/${property}`,
        method :'get'
    })
}

export function setHumidifiedPropertyApi(property){
    return request({
        url:`/iot/setHumidifiedProperty/${property}`,
        method :'get'
    })
}
export function setAutoTemperature(params={}){
    return request({
        url:'/iot/setAutoTemperature',
        method :'post',
        params:params,
    })
}

export function setAutoHumidity(params={}){
    return request({
        url:'/iot/setAutoHumidity',
        method :'post',
        params:params,
    })
}

export function setAutoBrightness(params={}){
    return request({
        url:'/iot/setAutoBrightness',
        method :'post',
        params:params,
    })
}

export function getAttributeThreshold(){
    return request({
        url:`/iot/getAttributeThreshold/`,
        method :'get'
    })
}

export function getHistoricalData(){
    return request({
        url:`/iot/getHistoricalData/`,
        method :'get'
    })
}
