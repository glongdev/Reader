package com.glong.reader.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Copyright (c) wangfei
 * Created by Garrett on 2018/11/22.
 * contact me krouky@outlook.com
 */
public class NetUtil {

    private static final String END = "\r\n";
    public static int connectionTimeOut = 30000;
    public static int readSocketTimeOut = 30000;

    public static String get(Request request) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(buildGetUrl(request.getBaseUrl(), request.getParam()));
            httpURLConnection = openUrlConnection(url);
            normalSetting(httpURLConnection, "GET", request.getHeaders());
            if (httpURLConnection == null) {
                return null;
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                String contentEncoding = httpURLConnection.getContentEncoding();
                InputStream stream = null;
                try {
                    stream = wrapStream(contentEncoding, inputStream);
                    String data = convertStreamToString(stream);
                    return data;
                } catch (IOException e) {
                    return "";
                } finally {
                    closeQuietly(stream);
                }

            }
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static InputStream wrapStream(String contentEncoding, InputStream inputStream)
            throws IOException {
        if (contentEncoding == null || "identity".equalsIgnoreCase(contentEncoding)) {
            return inputStream;
        }
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(inputStream);
        }
        if ("deflate".equalsIgnoreCase(contentEncoding)) {
            return new InflaterInputStream(inputStream, new Inflater(false), 512);
        }
        throw new RuntimeException("unsupported content-encoding: " + contentEncoding);
    }

    private static String buildGetUrl(String urlPath, Map<String, Object> params) {
        if (TextUtils.isEmpty(urlPath) || params == null || params.size() == 0) {
            return urlPath;
        }

        if (!urlPath.endsWith("?")) {
            urlPath += "?";
        }

        String paramsStr = buildGetParams(params);

        StringBuilder sbUrl = new StringBuilder(urlPath);
        sbUrl.append(paramsStr);
        return sbUrl.toString();
    }

    private static String buildGetParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (params.get(key) == null) {
                continue;
            }
            sb = sb.append(key + "=" + URLEncoder.encode(params.get(key).toString()) + "&");
        }

        String paramsStr = sb.substring(0, sb.length() - 1).toString();
        return paramsStr;
    }

    private static void normalSetting(HttpURLConnection urlConnection, String method, Map<String, String> mHeaders) throws ProtocolException {

        urlConnection.setConnectTimeout(connectionTimeOut);
        urlConnection.setReadTimeout(readSocketTimeOut);
        urlConnection.setRequestMethod(method);
        if (method.equals("GET")) {
            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            if (mHeaders != null && mHeaders.size() > 0) {
                Set<String> stringKeys = mHeaders.keySet();
                for (String key : stringKeys) {
                    urlConnection.setRequestProperty(key, mHeaders.get(key));
                }
            }
        } else if (method.equals("POST")) {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
        }
    }

    private static String convertStreamToString(InputStream is) {
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader, 512);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(inputStreamReader);
            closeQuietly(reader);
        }
        return stringBuilder.toString();
    }

    private static void closeQuietly(Closeable io) {
        try {
            if (io != null) {
                io.close();
            }
        } catch (IOException e) {
        }
    }

    public static String post(Request request) {
        String boundary = UUID.randomUUID().toString();
        HttpURLConnection httpURLConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        URL url = null;
        try {
            url = new URL(request.getBaseUrl());
            httpURLConnection = openUrlConnection(url);
            normalSetting(httpURLConnection, "POST", request.getHeaders());
//            if (req.mMimeType != null) {// stats cache log request
//                String data = (String) bodyPair.get("data");
//                httpURLConnection.setRequestProperty("Content-Type", req.mMimeType.toString());
//                outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(data.getBytes());
//            } else
            if (request.getParam() != null && request.getParam().size() > 0) {
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                outputStream = httpURLConnection.getOutputStream();
                addBodyParams(request.getParam(), request.getFilePair(), outputStream, boundary);
            } else {

//                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                Uri.Builder builder = new Uri.Builder();
//                builder.appendQueryParameter("content", message);
//                String query = builder.build().getEncodedQuery();
//                outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//                outputStream.write(query.getBytes());
            }
            outputStream.flush();
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                String contentEncoding = httpURLConnection.getContentEncoding();
                InputStream stream = wrapStream(contentEncoding, inputStream);
                String data = convertStreamToString(stream);
                return data;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static HttpURLConnection openUrlConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection;
        String scheme = url.getProtocol();
        boolean isHttpsRequest = false;
        if ("https".equals(scheme)) {
            isHttpsRequest = true;
        }
        if (isHttpsRequest) {
            httpURLConnection = (HttpsURLConnection) (url).openConnection();
            // TODO 处理https证书 1,需要测试https请求;2如需设置证书，需验证是否会对其它https请求有影响
            //trustHosts((HttpsURLConnection) urlConnection);
        } else {
            httpURLConnection = (HttpURLConnection) (url).openConnection();
        }
        return httpURLConnection;
    }

    private static void addBodyParams(Map<String, Object> map, Map<String, FilePair> filePair, OutputStream outputStream, String boundary) throws IOException {
        boolean didWriteData = false;
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> bodyPair = map;
        Set<String> keys = bodyPair.keySet();
        for (String key : keys) {
            if (bodyPair.get(key) != null) {
                addFormField(stringBuilder, key, bodyPair.get(key).toString(), boundary);
            }
        }

        if (stringBuilder.length() > 0) {
            didWriteData = true;
            outputStream = new DataOutputStream(outputStream);
            outputStream.write(stringBuilder.toString().getBytes());
        }

        // upload files like POST files to server
        if (filePair != null && filePair.size() > 0) {
            Set<String> fileKeys = filePair.keySet();
            for (String key : fileKeys) {
                FilePair pair = filePair.get(key);
                byte[] data = pair.mBinaryData;
                if (data == null || data.length < 1) {
                    continue;
                } else {
                    didWriteData = true;
                    addFilePart(pair.mFileName, data, boundary, outputStream);
                }
            }
        }

        if (didWriteData) {
            finishWrite(outputStream, boundary);
        }
    }

    private static void addFormField(StringBuilder writer, final String name, final String value, String boundary) {
        writer.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(END)
                .append("Content-Type: text/plain; charset=").append("UTF-8")
                .append(END).append(END).append(value).append(END);
    }


    private static void addFilePart(final String fieldName, byte[] data, String boundary, OutputStream outputStream)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"")
                .append("pic").append("\"; filename=\"").append(fieldName)
                .append("\"").append(END).append("Content-Type: ")
                .append("application/octet-stream").append(END)
                .append("Content-Transfer-Encoding: binary").append(END)
                .append(END);
        outputStream.write(stringBuilder.toString().getBytes());
        outputStream.write(data);
        outputStream.write(END.getBytes());
    }

    private static void finishWrite(OutputStream outputStream, String boundary) throws IOException {
        outputStream.write(END.getBytes());
        outputStream.write(("--" + boundary + "--").getBytes());
        outputStream.write(END.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    public static String download(Request request) {
        if (request.getMethod().equals("GET")) {
            return get(request);
        } else if (request.getMethod().equals("POST")) {
            return post(request);
        }
        return null;
    }
}
