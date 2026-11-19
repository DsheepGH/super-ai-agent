package com.agent.superaiagent.agent;

/**
 * ReAct （Reasoning and Acting） 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     * @return 是否需要执行行动，true标识需要执行，false表示不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     * @return 行动执行的结果
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            //先思考
            boolean shouldAct = think();
            if (!shouldAct) {
                return getNoActionResponse();
            }
            //再行动
            return act();
        } catch (Exception e) {
            //记录异常日志信息
            e.printStackTrace();
            return "步骤执行失败：" + e.getMessage();
        }
    }

    /**
     * 无需调用工具时的最终回复。工具调用型智能体会用模型正文覆盖它。
     */
    protected String getNoActionResponse() {
        return "思考完成 - 无需行动";
    }
}
