<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="问题内容"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.title"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.userName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
<!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
        <a-button @click="batchDelete">删除</a-button>
<!--        <a-button @click="batchDelete1">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">

        <template slot="content" slot-scope="text">
          <a-tooltip v-if="text">
            <template slot="title">{{ text }}</template>
            <span class="table-text-ellipsis">{{ text }}</span>
          </a-tooltip>
          <span v-else>- -</span>
        </template>

        <template slot="userName" slot-scope="text">
          <span v-if="text">{{ text }}</span>
          <span v-else style="color: #999;">- -</span>
        </template>

        <template slot="userImages" slot-scope="text, record">
          <a-popover v-if="record.userImages">
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + record.userImages" />
            </template>
            <a-avatar shape="square" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + record.userImages" />
          </a-popover>
          <a-avatar v-else shape="square" icon="user" />
        </template>

        <template slot="answerCount" slot-scope="text">
          <a-badge v-if="text !== null && text !== undefined" :count="text" :numberStyle="{ backgroundColor: '#52c41a' }" />
          <span v-else style="color: #999;">- -</span>
        </template>

        <template slot="createdAt" slot-scope="text">
          <span v-if="text">{{ text }}</span>
          <span v-else style="color: #999;">- -</span>
        </template>

        <template slot="operation" slot-scope="text, record">
          <a-icon type="eye" theme="twoTone" twoToneColor="#1890ff" @click="showDetail(record)" title="查看详情" style="margin-right: 10px;"></a-icon>
<!--          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改"></a-icon>-->
        </template>
      </a-table>
    </div>
    <!-- 详情 Modal -->
    <a-modal
      v-model="detailModal.visible"
      :title="detailModal.title"
      :width="1000"
      :footer="null"
      @cancel="handleDetailClose"
      centered
      :bodyStyle="{ padding: '24px' }">
      <div class="detail-container">
        <div class="question-header">
          <div class="question-title">
            <a-icon type="question-circle" style="color: #1890ff; font-size: 20px; margin-right: 12px;" />
            <span>{{ detailModal.data.title || '问题标题' }}</span>
          </div>
          <div class="question-meta">
            <div class="meta-item">
              <a-avatar v-if="detailModal.data.userImages" shape="circle" size="40" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + detailModal.data.userImages" />
              <a-avatar v-else shape="circle" size="40" icon="user" />
              <span class="user-name">{{ detailModal.data.userName || '匿名用户' }}</span>
            </div>
            <div class="meta-item">
              <a-icon type="clock-circle" style="margin-right: 4px;" />
              <span>{{ detailModal.data.createdAt || '- -' }}</span>
            </div>
          </div>
        </div>

        <a-divider style="margin: 16px 0" />

        <div class="question-content-section">
          <div class="section-title">
            <a-icon type="file-text" style="color: #faad14; margin-right: 8px;" />
            <span>问题内容</span>
          </div>
          <div class="section-body question-detail">
            <p class="text-content">{{ detailModal.data.questionContent || detailModal.data.content || '暂无内容' }}</p>
          </div>
        </div>

        <a-divider style="margin: 24px 0" />

        <div class="answers-section">
          <div class="section-title">
            <a-icon type="message" style="color: #52c41a; margin-right: 8px;" />
            <span>回答列表</span>
          </div>
          <div v-if="detailModal.answers && detailModal.answers.length > 0" class="answers-list">
            <div v-for="(answer, index) in detailModal.answers" :key="index" class="answer-item">
              <div class="answer-header">
                <div class="answer-user">
                  <a-avatar v-if="answer.userImages" shape="circle" size="32" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + answer.userImages" />
                  <a-avatar v-else shape="circle" size="32" icon="user" />
                  <span class="answer-user-name">{{ answer.userName || '匿名用户' }}</span>
                  <a-icon type="clock-circle" style="margin-left: 12px; color: #999;" />
                  <span class="answer-time">{{ answer.createdAt || '- -' }}</span>
                </div>
              </div>
              <div class="answer-content">
                <p>{{ answer.content || '暂无回答内容' }}</p>
              </div>
            </div>
          </div>
          <div v-else class="no-answers">
            <a-empty description="暂无回答" />
          </div>
        </div>
      </div>
    </a-modal>
    <bulletin-add
      v-if="bulletinAdd.visiable"
      @close="handleBulletinAddClose"
      @success="handleBulletinAddSuccess"
      :bulletinAddVisiable="bulletinAdd.visiable">
    </bulletin-add>
    <bulletin-edit
      ref="bulletinEdit"
      @close="handleBulletinEditClose"
      @success="handleBulletinEditSuccess"
      :bulletinEditVisiable="bulletinEdit.visiable">
    </bulletin-edit>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BulletinAdd from './QuestionsAdd.vue'
import BulletinEdit from './QuestionsEdit.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {BulletinAdd, BulletinEdit, RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: [],
      detailModal: {
        visible: false,
        title: '问题详情',
        data: {},
        answers: []
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '问题内容',
        dataIndex: 'title',
        ellipsis: true,
        scopedSlots: { customRender: 'title' },
        width: 250
      }, {
        title: '答疑内容',
        dataIndex: 'content',
        ellipsis: true,
        scopedSlots: { customRender: 'content' },
        width: 400
      }, {
        title: '用户名称',
        ellipsis: true,
        dataIndex: 'userName',
        scopedSlots: { customRender: 'userName' },
        width: 120
      }, {
        title: '头像',
        dataIndex: 'userImages',
        ellipsis: true,
        scopedSlots: { customRender: 'userImages' },
        width: 100
      }, {
        title: '回答数',
        dataIndex: 'answerCount',
        ellipsis: true,
        scopedSlots: { customRender: 'answerCount' },
        width: 80
      }, {
        title: '创建时间',
        dataIndex: 'createdAt',
        ellipsis: true,
        scopedSlots: { customRender: 'createdAt' },
        width: 180
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' },
        width: 120,
        fixed: 'right'
      }]
    }
  },
  mounted () {
    this.fetch()
    this.queryAnswersByQuestionId(1)
  },
  methods: {
    showDetail (record) {
      this.detailModal.data = { ...record }
      // 如果有回答数据，也一并赋值
      // if (record.answers) {
      //   this.detailModal.answers = record.answers
      // } else {
      //   this.detailModal.answers = []
      // }
      this.$get('/cos/party-answers/queryAnswersByQuestionId', {
        questionId: record.id
      }).then((r) => {
        this.detailModal.answers = r.data.data
      })
      this.detailModal.visible = true
    },
    handleDetailClose () {
      this.detailModal.visible = false
      this.detailModal.data = {}
      this.detailModal.answers = []
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增答疑成功')
      this.search()
    },
    edit (record) {
      this.$refs.bulletinEdit.setFormValues(record)
      this.bulletinEdit.visiable = true
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改答疑成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete1 () {
      this.$get('/cos/supplier-info/batchEditSupplierName').then((r) => {
      })
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/party-questions/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      this.$get('/cos/party-questions/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";

.table-text-ellipsis {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-container {
  .question-header {
    .question-title {
      display: flex;
      align-items: center;
      font-size: 20px;
      font-weight: 600;
      color: #262626;
      margin-bottom: 16px;
      padding: 12px 16px;
      background: #f5f5f5;
      border-radius: 6px;
      border-left: 4px solid #1890ff;
    }

    .question-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: #fafafa;
      border-radius: 6px;

      .meta-item {
        display: flex;
        align-items: center;

        .user-name {
          margin-left: 12px;
          font-size: 14px;
          font-weight: 500;
          color: #262626;
        }

        span {
          color: #8c8c8c;
          font-size: 13px;
        }
      }
    }
  }

  .question-content-section,
  .answers-section {
    .section-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
      color: #262626;
      margin-bottom: 12px;
      padding: 8px 12px;
      background: #f5f5f5;
      border-radius: 4px;
      border-left: 4px solid #1890ff;
    }

    .section-body {
      padding: 16px;
      background: #fafafa;
      border-radius: 6px;
      border: 1px solid #e8e8e8;
      min-height: 80px;

      &.question-detail {
        background: #f6ffed;
        border-color: #b7eb8f;
        border-left: 4px solid #52c41a;

        .text-content {
          margin: 0;
          line-height: 1.8;
          color: #262626;
          font-size: 14px;
          white-space: pre-wrap;
          word-break: break-all;
          max-height: 400px;
          overflow-y: auto;
        }
      }
    }
  }

  .answers-list {
    .answer-item {
      margin-bottom: 16px;
      padding: 16px;
      background: #fff;
      border: 1px solid #e8e8e8;
      border-radius: 6px;

      .answer-header {
        margin-bottom: 12px;

        .answer-user {
          display: flex;
          align-items: center;

          .answer-user-name {
            margin-left: 8px;
            font-size: 14px;
            font-weight: 500;
            color: #262626;
          }

          .answer-time {
            color: #999;
            font-size: 12px;
          }
        }
      }

      .answer-content {
        padding: 12px;
        background: #fafafa;
        border-radius: 4px;

        p {
          margin: 0;
          line-height: 1.8;
          color: #262626;
          font-size: 14px;
          white-space: pre-wrap;
          word-break: break-all;
        }
      }
    }
  }

  .no-answers {
    text-align: center;
    padding: 40px 0;
  }
}
</style>
