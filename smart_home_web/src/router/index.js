import Vue from 'vue'
import VueRouter from 'vue-router'
import SmartHome from "@/views/SmartHome.vue";
import HistoricalDate from "@/views/HistoricalDate.vue";


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'smartHome',
    component: SmartHome
  },
  {
    path: '/historicalDate',
    name: 'historicalDate',
    component: HistoricalDate
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
