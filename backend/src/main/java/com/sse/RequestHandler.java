package com.sse;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * As per docs
 * <a href="https://github.com/awslabs/aws-serverless-java-container/wiki/Quick-start---Spring-Boot3">...</a>
 * */
public class RequestHandler implements RequestStreamHandler  {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            log.info("before handler");
            handler = SpringBootLambdaContainerHandler
                    .getAwsProxyHandler(Application.class);
            log.info("after handler");
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            log.error("lambda couldn't start spring application {}", e.getMessage());
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream i, OutputStream o, Context c) throws IOException {
        handler.proxyStream(i, o, c);
    }

}