package org.semanticcloud.agentGateway;

public interface Command {
	void executeCommand(ConversationRegistry registry, MessageSender messageSender);

	String getConversationId();
}
