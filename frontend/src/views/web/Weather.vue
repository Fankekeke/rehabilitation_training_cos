
<template>
  <div style="width: 100%; padding: 0 1rem">
    <p style="font-size: 22px; margin-bottom: 1.5rem;">欢迎使用康复训练系统</p>

    <div v-if="weatherData" class="weather-container">
      <a-card :bordered="false" style="margin-bottom: 1.5rem;">
        <div slot="title" style="font-size: 18px; font-weight: bold;">
          {{ cityInfo.city }} - 实时天气
        </div>

        <a-row :gutter="16">
          <a-col :span="6">
            <div class="weather-item">
              <p class="label">当前温度</p>
              <p class="value">{{ weatherData.wendu }}°C</p>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="weather-item">
              <p class="label">湿度</p>
              <p class="value">{{ weatherData.shidu }}</p>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="weather-item">
              <p class="label">空气质量</p>
              <p class="value" :class="getQualityClass(weatherData.quality)">{{ weatherData.quality }}</p>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="weather-item">
              <p class="label">PM2.5</p>
              <p class="value">{{ weatherData.pm25 }}</p>
            </div>
          </a-col>
        </a-row>

        <a-divider />

        <a-alert
          :message="weatherData.ganmao"
          type="info"
          show-icon          style="margin-bottom: 1rem;"
        />
      </a-card>

      <a-card :bordered="false">
        <div slot="title" style="font-size: 18px; font-weight: bold;">
          未来天气预报
        </div>

        <a-table
          :columns="columns"
          :data-source="forecastList"
          :pagination="false"
          row-key="ymd"
          size="middle"
        >
          <span slot="date" slot-scope="text, record">
            {{ record.ymd }} ({{ record.week }})
          </span>
          <span slot="type" slot-scope="text, record">
            <a-tag :color="getWeatherColor(record.type)">{{ record.type }}</a-tag>
          </span>
          <span slot="temperature" slot-scope="text, record">
            {{ record.low }} ~ {{ record.high }}
          </span>
          <span slot="wind" slot-scope="text, record">
            {{ record.fx }} {{ record.fl }}
          </span>
          <span slot="aqi" slot-scope="text, record">
            <a-badge
              :status="getAqiStatus(record.aqi)"
              :text="record.aqi.toString()"
            />
          </span>
          <span slot="notice" slot-scope="text, record">
            {{ record.notice }}
          </span>
        </a-table>
      </a-card>
    </div>

    <div v-else-if="!loading" style="text-align: center; padding: 3rem;">
      <a-empty description="暂无天气数据" />
    </div>

    <div v-if="loading" style="text-align: center; padding: 3rem;">
      <a-spin size="large" tip="加载中..." />
    </div>
  </div>
</template>
<script>import axios from 'axios'

export default {
  data () {
    return {
      loading: true,
      citys: [],
      dataSource: [],
      storage: [],
      areaId: '',
      weatherData: null,
      cityInfo: {},
      forecastList: [],
      columns: [
        {
          title: '日期',
          dataIndex: 'ymd',
          key: 'date',
          scopedSlots: { customRender: 'date' },
          width: 180
        },
        {
          title: '天气',
          dataIndex: 'type',
          key: 'type',
          scopedSlots: { customRender: 'type' },
          width: 100
        },
        {
          title: '温度',
          dataIndex: 'high',
          key: 'temperature',
          scopedSlots: { customRender: 'temperature' },
          width: 150
        },
        {
          title: '风向风力',
          dataIndex: 'fx',
          key: 'wind',
          scopedSlots: { customRender: 'wind' },
          width: 150
        },
        {
          title: 'AQI指数',
          dataIndex: 'aqi',
          key: 'aqi',
          scopedSlots: { customRender: 'aqi' },
          width: 100
        },
        {
          title: '提示',
          dataIndex: 'notice',
          key: 'notice',
          scopedSlots: { customRender: 'notice' }
        }
      ]
    }
  },
  mounted () {
    axios.get('../../../static/file/city.json').then((r) => {
      this.citys = r.data
    })
    this.searchWeather()
  },
  methods: {
    handleSearch (value) {
      this.dataSource = []
      this.storage = []
      this.areaId = ''
      if (!value) {
        return
      }
      for (let i = 0; i < this.citys.length; i++) {
        let currentCity = this.citys[i]
        if (currentCity.countyname.indexOf(value) !== -1) {
          this.dataSource.push(currentCity.countyname)
          this.storage.push(currentCity.areaid)
        }
      }
    },
    onSelect (value) {
      let index = this.dataSource.indexOf(value)
      this.areaId = this.storage[index]
    },
    searchWeather () {
      this.loading = true
      this.$get('weather?areaId=' + this.areaId).then((r) => {
        if (r.data && r.data.code === 0) {
          const data = r.data.data
          this.weatherData = data.data
          this.cityInfo = data.cityInfo
          this.forecastList = data.data.forecast || []
        }
        this.loading = false
      }).catch((r) => {
        console.error(r)
        this.$message.error('天气查询失败')
        this.loading = false
      })
    },
    getQualityClass (quality) {
      const qualityMap = {
        '优': 'quality-excellent',
        '良': 'quality-good',
        '轻度污染': 'quality-light-pollution',
        '中度污染': 'quality-moderate-pollution',
        '重度污染': 'quality-heavy-pollution'
      }
      return qualityMap[quality] || ''
    },
    getWeatherColor (type) {
      const colorMap = {
        '晴': 'orange',
        '多云': 'blue',
        '阴': 'gray',
        '小雨': 'green',
        '中雨': 'cyan',
        '大雨': 'purple',
        '暴雨': 'red',
        '霾': 'orange'
      }
      return colorMap[type] || 'default'
    },
    getAqiStatus (aqi) {
      if (aqi <= 50) return 'success'
      if (aqi <= 100) return 'processing'
      if (aqi <= 150) return 'warning'
      return 'error'
    }
  }
}
</script>
<style lang="less">  .global-search-main {
  margin-bottom: 2.5rem;
  .global-search-wrapper {
    width: 300px;
    margin:0 auto;
    .global-search {
      width: 100%;
    }
  }
}
.weather-area {
  display: inline;
  .weather-chart-info {
    width: 49%;
    display:inline-block;
  }
  .weather-info {
    margin: .5rem 0;
    width: 100%;
    display: inline-block;
    p {
      margin-bottom: .4rem !important;
    }
  }
}
.global-search.ant-select-auto-complete {
  .ant-select-selection--single {
    margin-right: -46px;
  }
  .ant-input-affix-wrapper {
    .ant-input:not(:last-child) {
      padding-right: 62px;
    }
    .ant-input-suffix {
      right: 0;
    }
    .ant-input-suffix button {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
    }
  }
}
.weather-container {
  .weather-item {
    text-align: center;
    padding: 1rem 0;

    .label {
      font-size: 14px;
      color: #666;
      margin-bottom: 0.5rem;
    }

    .value {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin: 0;

      &.quality-excellent {
        color: #52c41a;
      }

      &.quality-good {
        color: #1890ff;
      }

      &.quality-light-pollution {
        color: #faad14;
      }

      &.quality-moderate-pollution {
        color: #ff7a45;
      }

      &.quality-heavy-pollution {
        color: #f5222d;
      }
    }
  }
}
</style>
