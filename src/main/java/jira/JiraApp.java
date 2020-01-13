package jira;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;



@SpringBootApplication
public class JiraApp implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(JiraApp.class);
    
	public static void main(String[] args) {
		SpringApplication.run(JiraApp.class, args);
	}
	


	@Override
	public void run(String... args) throws Exception {
		JiraRestClient jiraRestClient =  new AsynchronousJiraRestClientFactory()
					.createWithBasicHttpAuthentication(new URI("http://localhost:8080"), "art", "3472ans1");
		
		
		String key = "CET-2";
		
		IssueRestClient jiraClient = jiraRestClient.getIssueClient();
		Promise<Issue>  issue = jiraClient.getIssue(key);
		
		JSONObject defaultValueField1 = new JSONObject(new HashMap<String, String>() {{put("value","");}});
		
		JSONObject myField1 = Optional.ofNullable((JSONObject)issue.get().getFieldByName("MyFiled1").getValue())
													.orElse(defaultValueField1);
		
		LOG.info(String.format("Issue %s summary: %s", key, issue.get().getSummary()));
		LOG.info(String.format("Issue %s MyFiled1: %s", key, myField1.getString("value")));
		
		IssueInput newIssue = new IssueInputBuilder()
			      .setSummary(String.format("My %s updated summary", key))
			      	.build();
		
		
		jiraRestClient.getIssueClient().updateIssue(key, newIssue);
	}

}
