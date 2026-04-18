
<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']" style="width: 68%;margin: 0 auto">
    <a-card :bordered="false" style="margin-left: -14px;margin-right: -14px;padding: 12px;">
      <div class="staff-container">
        <a-row :gutter="24">
          <!-- 左侧康复师列表 -->
          <a-col :span="8">
            <div class="staff-list-panel">
              <div class="panel-header">
                <h3>推荐康复师</h3>
              </div>
              <div class="staff-list">
                <div
                  v-for="item in staffList"
                  :key="item.staff.id"
                  class="staff-item"
                  :class="{ active: selectedStaff && selectedStaff.staff.id === item.staff.id }"
                  @click="selectStaff(item)"
                >
                  <div class="staff-avatar">
                    <img
                      :src="getAvatarUrl(item.staff.images)"
                      :alt="item.staff.name"
                      v-if="item.staff.images"
                    >
                    <span v-else>{{ item.staff.name.charAt(0) }}</span>
                  </div>
                  <div class="staff-info">
                    <div class="staff-name">{{ item.staff.name }}</div>
                    <div class="staff-detail">
                      <span v-if="item.staff.sex == '1'">男</span>
                      <span v-else>女</span>
                      <span class="divider">|</span>
                      <span>{{ getDiplomaText(item.staff.diploma) }}</span>
                    </div>
                    <div class="staff-stats">
                      <a-badge :count="item.orderCount" :numberStyle="{ backgroundColor: '#52c41a' }">
                        <span class="stat-text">订单数</span>
                      </a-badge>
                    </div>
                  </div>
                </div>
                <div v-if="staffList.length === 0" class="empty-tip">
                  <a-empty description="暂无推荐康复师" />
                </div>
              </div>
            </div>
          </a-col>

          <!-- 右侧详情区域 -->
          <a-col :span="16">
            <div v-if="selectedStaff" class="staff-detail-panel">
              <!-- 康复师基本信息 -->
              <div class="detail-header">
                <div class="header-avatar">
                  <img
                    :src="getAvatarUrl(selectedStaff.staff.images)"
                    :alt="selectedStaff.staff.name"
                    v-if="selectedStaff.staff.images"
                  >
                  <span v-else>{{ selectedStaff.staff.name.charAt(0) }}</span>
                </div>
                <div class="header-info">
                  <h2>{{ selectedStaff.staff.name }}</h2>
                  <div class="info-row">
                    <a-tag color="blue">{{ selectedStaff.staff.sex == '1' ? '男' : '女' }}</a-tag>
                    <a-tag color="green">{{ getDiplomaText(selectedStaff.staff.diploma) }}</a-tag>
                    <a-tag color="purple">{{ selectedStaff.staff.schoolName }}</a-tag>
                  </div>
                  <div class="info-row contact-info">
                    <span><a-icon type="phone" /> {{ selectedStaff.staff.phone }}</span>
                    <span><a-icon type="mail" /> {{ selectedStaff.staff.mail }}</span>
                    <span><a-icon type="environment" /> {{ selectedStaff.staff.address }}</span>
                  </div>
                </div>
                <div class="header-actions">
                  <a-button type="primary" icon="message" @click="startChat">
                    在线沟通
                  </a-button>
                </div>
              </div>

              <!-- 康复项目列表 -->
              <div class="service-section">
                <div class="section-title">
                  <h3>康复协助项目</h3>
                  <a-badge :count="selectedStaff.orderReserveList ? selectedStaff.orderReserveList.length : 0" />
                </div>

                <div v-if="selectedStaff.orderReserveList && selectedStaff.orderReserveList.length > 0" class="service-list">
                  <a-card
                    v-for="service in selectedStaff.orderReserveList"
                    :key="service.id"
                    class="service-card"
                    hoverable
                  >
                    <div class="service-content">
                      <div class="service-image">
                        <img
                          :src="getServiceImageUrl(service.images)"
                          :alt="service.content"
                          v-if="service.images"
                        >
                        <a-icon type="picture" v-else />
                      </div>
                      <div class="service-info">
                        <div class="service-type">
                          <a-tag :color="getTypeColor(service.type)">
                            {{ getTypeText(service.type) }}
                          </a-tag>
                        </div>
                        <div class="service-description">{{ service.content }}</div>
                        <div class="service-footer">
                          <div class="service-price">
                            <span class="price-label">价格：</span>
                            <span class="price-value">¥{{ service.totalPrice }}</span>
                          </div>
                          <div class="service-actions">
                            <a-button type="primary" size="small" @click="purchaseService(service)">
                              立即购买
                            </a-button>
<!--                            <a-button size="small" icon="message" @click="chatAboutService(service)">-->
<!--                              咨询-->
<!--                            </a-button>-->
                          </div>
                        </div>
                      </div>
                    </div>
                  </a-card>
                </div>

                <div v-else class="empty-service">
                  <a-empty description="该康复师暂无康复项目" />
                </div>
              </div>
            </div>

            <div v-else class="no-selection">
              <a-empty description="请选择康复师查看详情">
                <a-icon type="user" slot="image" style="font-size: 64px; color: #d9d9d9;" />
              </a-empty>
            </div>
          </a-col>
        </a-row>
      </div>
    </a-card>
  </div>
</template>

<script>import {mapState} from "vuex";

export default {
  name: "Staff",
  data () {
    return {
      staffList: [],
      selectedStaff: null
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      currentUser: state => state.account.user
    }),
  },
  mounted () {
    this.queryStaffListRecommend()
  },
  methods: {
    queryStaffListRecommend () {
      this.$get('/cos/staff-info/queryStaffListRecommend', {
        userId: this.currentUser.userId
      }).then((r) => {
        if (r.data && r.data.code === 0) {
          this.staffList = r.data.data || []
        }
      })
    },

    selectStaff (item) {
      this.selectedStaff = item
    },

    getAvatarUrl (images) {
      if (!images) return ''
      const imageUrl = Array.isArray(images) ? images[0] : images
      return `http://127.0.0.1:9527/imagesWeb/${imageUrl}`
    },

    getServiceImageUrl (images) {
      if (!images) return ''
      const imageUrl = Array.isArray(images) ? images[0] : images
      return `http://127.0.0.1:9527/imagesWeb/${imageUrl}`
    },

    getDiplomaText (diploma) {
      const diplomaMap = {
        '1': '本科',
        '2': '硕士',
        '3': '博士',
        '4': '专科'
      }
      return diplomaMap[diploma] || '未知'
    },

    getTypeText (type) {
      const typeMap = {
        '1': '力量性运动',
        '2': '耐力性运动',
        '3': '柔韧性运动',
        '4': '微举重训练',
        '5': '双手臂训练',
        '6': '胸部肌肉训练',
        '7': '运动心肺训练',
        '8': '脊柱矫正训练'
      }
      return typeMap[type] || '其他'
    },

    getTypeColor (type) {
      const colorMap = {
        '1': 'blue',
        '2': 'green',
        '3': 'orange',
        '4': 'purple',
        '5': 'cyan',
        '6': 'pink',
        '7': 'red',
        '8': 'geekblue'
      }
      return colorMap[type] || 'default'
    },

    purchaseService (service) {
      this.$confirm({
        title: '确认购买',
        content: `确定要购买该康复项目吗？价格：¥${service.totalPrice}`,
        okText: '确认支付',
        cancelText: '取消',
        onOk: () => {
          this.submitOrder(service)
        }
      })
    },

    submitOrder (service) {
      let orderData = {
        userId: this.currentUser.userId,
        staffId: this.selectedStaff.staff.id,
        type: service.type,
        totalPrice: service.totalPrice,
        content: service.content,
        images: service.images
      }

      this.$post('/cos/order-reserve/create', orderData).then((r) => {
        if (r.data && r.data.code === 0) {
          this.$message.success('订单创建成功')
          this.payOrder(r.data.data)
        } else {
          this.$message.error('订单创建失败')
        }
      })
    },

    payOrder (order) {
      let data = {
        outTradeNo: order.code,
        subject: `${order.createDate}康复项目缴费`,
        totalAmount: order.totalPrice,
        body: order.content
      }
      this.$post('/cos/pay/alipay', data).then((r) => {
        const divForm = document.getElementsByTagName('div')
        if (divForm.length) {
          document.body.removeChild(divForm[0])
        }
        const div = document.createElement('div')
        div.innerHTML = r.data.msg
        document.body.appendChild(div)
        document.forms[0].setAttribute('target', '_self')
        document.forms[0].submit()
      })
    },

    startChat () {
      this.$post('/cos/chat-record/defaultChat', {
        toUserId: this.selectedStaff.staff.sysUserId,
        userId: this.currentUser.userId,
        senderType: 0,
        content: '你好'
      }).then((r) => {
        // 跳转到聊天页面，并传递默认消息
        this.$router.push({
          path: '/chat'
        })
      })
    },

    chatAboutService (service) {
      this.$post('/cos/chat-record/defaultChat', {
        toUserId: this.selectedStaff.staff.sysUserId,
        userId: this.currentUser.userId,
        senderType: 0,
        content: '你好'
      }).then((r) => {
        // 跳转到聊天页面，并传递默认消息
        this.$router.push({
          path: '/chat'
        })
      })
    }
  },
}
</script>

<style scoped>.staff-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.staff-list-panel {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 112px);
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.panel-header h3 {
  margin: 0;
  color: #262626;
  font-size: 16px;
  font-weight: 600;
}

.staff-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.staff-item {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e8e8e8;
}

.staff-item:hover {
  background-color: #f5f5f5;
  border-color: #1890ff;
}

.staff-item.active {
  background-color: #e6f7ff;
  border-color: #1890ff;
}

.staff-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin-right: 16px;
  overflow: hidden;
  flex-shrink: 0;
}

.staff-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.staff-info {
  flex: 1;
  overflow: hidden;
}

.staff-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.staff-detail {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.divider {
  margin: 0 8px;
  color: #d9d9d9;
}

.staff-stats {
  display: flex;
  gap: 8px;
}

.stat-text {
  font-size: 12px;
  color: #595959;
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
}

.staff-detail-panel {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 112px);
  overflow-y: auto;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  padding: 24px;
  border-bottom: 1px solid #e8e8e8;
  background: linear-gradient(to bottom, #fafafa, #fff);
}

.header-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
  font-weight: bold;
  margin-right: 24px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.header-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-info {
  flex: 1;
}

.header-info h2 {
  margin: 0 0 12px 0;
  font-size: 24px;
  color: #262626;
  font-weight: 600;
}

.info-row {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.contact-info {
  font-size: 13px;
  color: #595959;
}

.contact-info span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.header-actions {
  flex-shrink: 0;
}

.service-section {
  padding: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title h3 {
  margin: 0;
  font-size: 18px;
  color: #262626;
  font-weight: 600;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-card {
  transition: all 0.3s;
}

.service-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.service-content {
  display: flex;
  gap: 16px;
}

.service-image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.service-image .anticon {
  font-size: 48px;
  color: #d9d9d9;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.service-type {
  margin-bottom: 8px;
}

.service-description {
  flex: 1;
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
  margin-bottom: 12px;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.service-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-label {
  font-size: 14px;
  color: #8c8c8c;
}

.price-value {
  font-size: 24px;
  font-weight: 600;
  color: #ff4d4f;
}

.service-actions {
  display: flex;
  gap: 8px;
}

.empty-service {
  padding: 60px 0;
  text-align: center;
}

.no-selection {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 112px);
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .staff-container {
    padding: 12px;
  }

  .detail-header {
    flex-direction: column;
  }

  .header-avatar {
    margin-bottom: 16px;
  }

  .service-content {
    flex-direction: column;
  }

  .service-image {
    width: 100%;
    height: 200px;
  }
}
</style>
