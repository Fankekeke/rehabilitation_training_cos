
<template>
  <div class="page-container" style="width: 100%;">
    <a-row :gutter="16" class="content-wrapper">
      <!-- 左侧问题列表 -->
      <a-col :span="16" class="questions-list">
        <a-card
          :loading="loading"
          :bordered="false"
          class="questions-card"
          :bodyStyle="{ padding: '16px' }"
        >
          <div slot="title" class="card-header">
            <a-icon type="message" theme="filled" style="color: #1890ff; font-size: 24px;" />
            <span class="header-title">康复情况问答</span>
            <a-divider type="vertical" style="height: 24px; margin: 0 12px;" />
            <span class="header-subtitle">所有用户的提问与回答</span>
          </div>

          <div class="question-items">
            <div
              v-for="(question, index) in dataSource"
              :key="question.id"
              class="question-item"
              @click="viewDetail(question)"
            >
              <div class="question-header">
                <div class="user-info">
                  <a-avatar :src="'http://127.0.0.1:9527/imagesWeb/' + question.userImages" size="large" />
                  <div class="user-detail">
                    <div class="username">{{ question.userName }}</div>
                    <div class="time">{{ question.createdAt }}</div>
                  </div>
                </div>
                <a-tag :color="question.status === '进行中' ? 'blue' : 'green'" size="large">
                  {{ question.status }}
                </a-tag>
              </div>

              <h3 class="question-title">{{ question.title }}</h3>

              <div class="question-content">
                {{ question.content.substring(0, 200) }}{{ question.content.length > 200 ? '...' : '' }}
              </div>

              <div class="question-footer">
                <div class="question-stats">
                  <a-badge :count="question.answerCount || 0" :numberStyle="{ backgroundColor: '#52c41a' }">
                    <span class="stat-text"><a-icon type="message" /> 回答</span>
                  </a-badge>
                </div>
                <a-button type="link" size="small">
                  查看详情 <a-icon type="right" />
                </a-button>
              </div>
            </div>

            <!-- 空状态 -->
            <a-empty
              v-if="!loading && dataSource.length === 0"
              description="暂无问答内容，快来提出第一个问题吧！"
              class="empty-state"
            />
          </div>
        </a-card>
      </a-col>

      <!-- 右侧操作区域 -->
      <a-col :span="8" class="operations-panel">
        <!-- 提问框 -->
        <a-card
          :bordered="false"
          class="ask-card"
          :bodyStyle="{ padding: '20px' }"
        >
          <div slot="title" class="ask-header">
            <a-icon type="edit" theme="filled" style="color: #722ed1; font-size: 20px;" />
            <span class="ask-title">我要提问</span>
          </div>

          <a-form :form="form" layout="vertical">
            <a-form-item label="问题标题">
              <a-input
                v-decorator="[
                  'title',
                  { rules: [{ required: true, message: '请输入问题标题' }] }
                ]"
                placeholder="简明扼要地描述你的问题"
                maxLength="100"
              />
            </a-form-item>

            <a-form-item label="问题内容">
              <a-textarea
                v-decorator="[
                  'content',
                  { rules: [{ required: true, message: '请输入问题内容' }] }
                ]"
                placeholder="详细描述你的问题，让大家更好地帮助你"
                :rows="6"
                maxLength="2000"
              />
            </a-form-item>

            <a-form-item>
              <a-button
                type="primary"
                block
                size="large"
                @click="handleSubmit"
                :loading="submitting"
                class="submit-btn"
              >
                <a-icon type="send" /> 发布问题
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 统计信息 -->
        <a-card
          :bordered="false"
          class="stats-card"
          :bodyStyle="{ padding: '16px' }"          style="margin-top: 16px;"
        >
          <a-statistic title="总问题数" :value="totalQuestions" :value-style="{ color: '#1890ff' }">
            <template #prefix>
              <a-icon type="book" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 详情 Modal -->
    <a-modal
      v-model="detailModal.visible"
      title="问答详情"
      width="900px"
      :footer="null"
      @cancel="handleDetailClose"
      class="detail-modal"
    >
      <div class="detail-content" v-if="detailModal.data">
        <div class="detail-question">
          <div class="question-user">
            <a-avatar :src="'http://127.0.0.1:9527/imagesWeb/' + detailModal.data.userImages" size="large" />
            <div class="user-info-detail">
              <div class="username">{{ detailModal.data.userName }}</div>
              <div class="time">{{ detailModal.data.createdAt }}</div>
            </div>
            <div class="question-actions">
              <a-tag :color="detailModal.data.status === '进行中' ? 'blue' : 'green'" size="large">
                {{ detailModal.data.status }}
              </a-tag>
              <a-button
                v-if="detailModal.data.sysUserId === currentUser.userId"
                type="primary"
                size="small"
                @click.stop="handleAcceptAnswer"
                :disabled="detailModal.data.status === '已采纳'"
              >
                <a-icon type="check-circle" /> 已采纳
              </a-button>
            </div>
          </div>

          <h2 class="detail-title">{{ detailModal.data.title }}</h2>

          <div class="detail-body">
            {{ detailModal.data.content }}
          </div>

          <div class="detail-stats">
            <a-badge :count="detailModal.data.answerCount || 0" :numberStyle="{ backgroundColor: '#52c41a' }">
              <span class="stat-text"><a-icon type="message" /> 回答数</span>
            </a-badge>
          </div>
        </div>

        <a-divider>回答列表</a-divider>

        <div class="answer-list">
          <a-comment
            v-for="answer in answerList"
            :key="answer.id"
            :author="answer.userName"
            :avatar="'http://127.0.0.1:9527/imagesWeb/' + answer.userImages"
            :datetime="answer.createdAt"
            :content="answer.content"
          >
            <template slot="actions">
              <span @click.stop="handleUpvote(answer)">
                <a-icon type="like" :theme="answer.userVote === 'up' ? 'filled' : 'outlined'" />
                {{ answer.upCount || 0 }}
              </span>
              <span @click.stop="handleDownvote(answer)">
                <a-icon type="dislike" :theme="answer.userVote === 'down' ? 'filled' : 'outlined'" />
                {{ answer.downCount || 0 }}
              </span>
            </template>
          </a-comment>

          <a-empty
            v-if="!answerLoading && answerList.length === 0"
            description="暂无回答，快来抢沙发吧！"
          />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>import { mapState } from 'vuex'

export default {
  name: 'Questions',
  data () {
    return {
      loading: false,
      submitting: false,
      dataSource: [],
      pagination: {
        defaultPageSize: 10,
        defaultCurrent: 1,
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      totalQuestions: 0,
      detailModal: {
        visible: false,
        data: {}
      },
      answerList: [],
      answerLoading: false,
      formLayout: 'vertical'
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    form () {
      return this.$form.createForm(this, { name: 'ask_form' })
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    viewDetail (record) {
      this.detailModal.data = { ...record }
      this.detailModal.visible = true
      this.queryAnswersByQuestionId(record.id)
    },
    queryVotesByUser (questionId) {
      this.$get('/cos/content-votes/queryVotesByUser', {
        userId: this.currentUser.userId,
        questionId
      }).then((r) => {
        if (r.data.code === 0 && r.data.data) {
          const voteRecords = r.data.data
          this.answerList = this.answerList.map(answer => {
            const vote = voteRecords.find(v => v.answerId === answer.id)
            if (vote) {
              return {
                ...answer,
                userVote: vote.voteType === 1 ? 'up' : vote.voteType === 0 ? 'down' : null
              }
            }
            return answer
          })
        }
      })
    },
    queryAnswersByQuestionId (questionId) {
      this.answerLoading = true
      this.$get('/cos/party-answers/queryAnswersByQuestionId/', {
        questionId
      }).then((r) => {
        if (r.data.code === 0) {
          this.answerList = r.data.data || []
          this.queryVotesByUser(questionId)
        } else {
          this.$message.error(r.data.msg || '获取回答列表失败')
        }
      }).catch((error) => {
        console.error('获取回答列表错误:', error)
        this.$message.error('网络错误，请稍后重试')
      }).finally(() => {
        this.answerLoading = false
      })
    },
    handleDetailClose () {
      this.detailModal.visible = false
      this.detailModal.data = {}
      this.answerList = []
    },
    handleAcceptAnswer () {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }
      this.$confirm({
        title: '确认采纳',
        content: '确定要采纳这个问题的答案吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$get('/cos/party-questions/acceptAnswer', {
            questionId: this.detailModal.data.id
          }).then((r) => {
            if (r.data.code === 0) {
              this.$message.success('采纳成功')
              this.detailModal.data.status = '已采纳'
              this.fetch()
            } else {
              this.$message.error(r.data.msg || '采纳失败')
            }
          }).catch((error) => {
            console.error('采纳问题错误:', error)
            this.$message.error('网络错误，请稍后重试')
          })
        }
      })
    },
    handleSubmit () {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }

      this.form.validateFields((err, values) => {
        if (!err) {
          this.submitting = true
          this.$post('/cos/party-questions', {
            userId: this.currentUser.userId,
            title: values.title,
            content: values.content,
            status: '进行中'
          }).then((r) => {
            if (r.data.code === 0) {
              this.$message.success('发布成功！')
              this.form.resetFields()
              this.fetch()
            } else {
              this.$message.error(r.data.msg || '发布失败')
            }
          }).catch((error) => {
            console.error('发布问题错误:', error)
            this.$message.error('网络错误，请稍后重试')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    handleUpvote (answer) {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }
      this.$post('/cos/content-votes', {
        answerId: answer.id,
        userId: this.currentUser.userId,
        voteType: 1
      }).then((r) => {
        if (r.data.code === 0) {
          this.$message.success('点赞成功')
          this.queryAnswersByQuestionId(this.detailModal.data.id)
        } else {
          this.$message.error(r.data.msg || '点赞失败')
        }
      }).catch((error) => {
        console.error('点赞错误:', error)
        this.$message.error('网络错误，请稍后重试')
      })
    },
    handleDownvote (answer) {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }
      this.$post('/cos/content-votes', {
        answerId: answer.id,
        userId: this.currentUser.userId,
        voteType: 0
      }).then((r) => {
        if (r.data.code === 0) {
          this.$message.success('点踩成功')
          this.queryAnswersByQuestionId(this.detailModal.data.id)
        } else {
          this.$message.error(r.data.msg || '点踩失败')
        }
      }).catch((error) => {
        console.error('点踩错误:', error)
        this.$message.error('网络错误，请稍后重试')
      })
    },
    fetch () {
      this.loading = true
      this.$get('/cos/party-questions/queryAllQuestions', {
        page: this.pagination.defaultCurrent,
        size: this.pagination.defaultPageSize
      }).then((r) => {
        console.log(r)
        if (r.data.code === 0 && r.data.data) {
          this.dataSource = r.data.data.records || r.data.data
          this.totalQuestions = r.data.data.total || this.dataSource.length
        }
      }).catch((error) => {
        console.error('获取问答列表失败:', error)
        this.$message.error('加载问答列表失败')
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped lang="less">.page-container {
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f3f3f3 100%);
  min-height: calc(100vh - 64px);
}

.content-wrapper {
  .questions-list {
    .questions-card {
      border-radius: 3px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);

      .card-header {
        display: flex;
        align-items: center;
        padding: 16px 0;
        border-bottom: 2px solid #f0f0f0;
        margin-bottom: 16px;

        .header-title {
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin-left: 12px;
        }

        .header-subtitle {
          font-size: 14px;
          color: #999;
          font-weight: normal;
        }
      }

      .question-items {
        .question-item {
          padding: 20px;
          margin-bottom: 16px;
          background: #fafafa;
          border-radius: 3px;
          border: 2px solid transparent;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #1890ff;
            background: #e6f7ff;
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(24, 144, 255, 0.15);
          }

          .question-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;

            .user-info {
              display: flex;
              align-items: center;
              gap: 12px;

              .user-detail {
                .username {
                  font-weight: 600;
                  color: #333;
                  font-size: 15px;
                }

                .time {
                  font-size: 12px;
                  color: #999;
                  margin-top: 2px;
                }
              }
            }
          }

          .question-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin: 0 0 12px 0;
            line-height: 1.5;
          }

          .question-content {
            color: #666;
            line-height: 1.8;
            margin-bottom: 16px;
            white-space: pre-line;
          }

          .question-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .question-stats {
              .stat-text {
                display: flex;
                align-items: center;
                gap: 6px;
                color: #666;
                font-size: 14px;
              }
            }
          }
        }

        .empty-state {
          padding: 60px 0;
        }
      }
    }
  }

  .operations-panel {
    .ask-card {
      border-radius: 3px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
      background: rgba(255, 255, 255, 0.95);

      .ask-header {
        display: flex;
        align-items: center;
        gap: 8px;

        .ask-title {
          font-size: 18px;
          font-weight: 600;
          color: #333;
        }
      }

      .submit-btn {
        height: 44px;
        font-size: 16px;
        border-radius: 3px;
        background: linear-gradient(135deg, #41e547 0%, #13b264 100%);
        border: none;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(114, 46, 209, 0.4);
        }
      }
    }

    .stats-card {
      border-radius: 3px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
      background: rgba(255, 255, 255, 0.95);

      /deep/ .ant-statistic-title {
        font-size: 14px;
        color: #666;
      }

      /deep/ .ant-statistic-content {
        font-size: 24px;
        font-weight: 600;
      }
    }
  }
}

.detail-modal {
  .detail-content {
    .detail-question {
      .question-user {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding-bottom: 16px;
        border-bottom: 2px solid #f0f0f0;

        .user-info-detail {
          flex: 1;
          margin-left: 12px;

          .username {
            font-weight: 600;
            color: #333;
            font-size: 16px;
          }

          .time {
            font-size: 13px;
            color: #999;
            margin-top: 4px;
          }
        }
      }

      .detail-title {
        font-size: 22px;
        font-weight: 600;
        color: #333;
        margin: 0 0 20px 0;
        line-height: 1.5;
      }

      .detail-body {
        color: #333;
        line-height: 1.8;
        white-space: pre-line;
        padding: 20px;
        background: #fafafa;
        border-radius: 3px;
        margin-bottom: 20px;
      }

      .detail-stats {
        .stat-text {
          display: flex;
          align-items: center;
          gap: 6px;
          color: #666;
          font-size: 14px;
        }
      }
    }

    .answer-list {
      max-height: 400px;
      overflow-y: auto;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .content-wrapper {
    flex-direction: column;

    .questions-list,
    .operations-panel {
      width: 100%;
    }

    .operations-panel {
      margin-top: 16px;
    }
  }
}
</style>
