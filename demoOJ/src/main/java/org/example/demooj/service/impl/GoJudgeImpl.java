package org.example.demooj.service.impl;

import jakarta.annotation.Resource;
import org.example.demooj.dto.JudgeResult;
import org.example.demooj.dto.LanguageRun;
import org.example.demooj.dto.ProblemStatus;
import org.example.demooj.dto.UserSubmit;
import org.example.demooj.dto.sandbox.*;
import org.example.demooj.entity.CompetitionInformation;
import org.example.demooj.entity.Problem;
import org.example.demooj.entity.User;
import org.example.demooj.entity.UserSubmittedLog;
import org.example.demooj.mapper.CompetitionMapper;
import org.example.demooj.mapper.UserMapper;
import org.example.demooj.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoJudgeImpl implements GoJudge {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ProblemOperation problemOperationImpl;
    @Resource
    private CompetitionOperation  competitionOperationImpl;
    @Resource
    private CompetitionMapper competitionMapper;
    @Resource
    private StatusAndRank statusAndRank;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LoginValidation  loginValidation;

    @Value("${file.input}")
    private String basePath;
    @Value("${dockerUrl}")
    private String baseUrl;

    @Override
    public JudgeResult judge(UserSubmit userSubmit) {
            Problem problem = problemOperationImpl.selectProblemById(userSubmit.getProblemId());
            LocalDateTime endTime = competitionMapper.selectCompetitionById(userSubmit.getCompetitionId()).getEndTime();
            UserSubmittedLog userSubmittedLog= UserSubmittedLog.builder()
                    .userId(userSubmit.getUserId())
                    .problemId(problem.getProblemId())
                    .submittedTime(userSubmit.getSubmittedTime())
                    .result("Queuing")
                    .memory(0L).runTime(0L).code(userSubmit.getContext())
                    .competitionId(userSubmit.getCompetitionId())
                    .language(userSubmit.getLanguageRun().getLanguage())
                    .build();
            statusAndRank.insertUserSubmittedLog(userSubmittedLog);
            if(endTime.isAfter(LocalDateTime.now())&&!loginValidation.isAdmin(userSubmit.getUserId())&&!statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId()))
            competitionOperationImpl.submitSumIncrement(userSubmit.getProblemId(),  userSubmit.getCompetitionId());
            Integer subId=userSubmittedLog.getId();
            if(userSubmit.getLanguageRun().getLanguage().equals("cpp"))
            {
                JudgeResult judgeResult= getResult(
                        problem.getTimeLimit(),
                        problem.getMemoryLimit(),
                        userSubmit.getContext(),
                        userSubmit.getProblemId(),
                        subId,
                        userSubmit.getCompetitionId()
                );
                if(endTime.isAfter(LocalDateTime.now())&&!loginValidation.isAdmin(userSubmit.getUserId())&&!statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId())&&judgeResult.getProblemStatus().equals(ProblemStatus.ACCEPTED))
                    competitionOperationImpl.acceptSumIncrement(userSubmit.getProblemId(),  userSubmit.getCompetitionId());
                statusAndRank.updateStatus(subId, judgeResult.getMemory(), judgeResult.getTime(), judgeResult.getProblemStatus().getStatus());
                if(endTime.isAfter(LocalDateTime.now()))
                rankOp(userSubmit,judgeResult);
                return  judgeResult;
            }
            else if(userSubmit.getLanguageRun().getLanguage().equals("java"))
            {
                JudgeResult judgeResult= getResultJava(
                        problem.getTimeLimit()*2,
                        problem.getMemoryLimit()*2,
                        userSubmit.getContext(),
                        userSubmit.getProblemId(),
                        subId,
                        userSubmit.getCompetitionId()
                );
                if(endTime.isAfter(LocalDateTime.now())&&!loginValidation.isAdmin(userSubmit.getUserId())&&!statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId())&&judgeResult.getProblemStatus().equals(ProblemStatus.ACCEPTED))
                    competitionOperationImpl.acceptSumIncrement(userSubmit.getProblemId(),  userSubmit.getCompetitionId());
                statusAndRank.updateStatus(subId, judgeResult.getMemory(), judgeResult.getTime(), judgeResult.getProblemStatus().getStatus());
                if(endTime.isAfter(LocalDateTime.now()))
                rankOp(userSubmit,judgeResult);
                return judgeResult;
            }
            else {
                JudgeResult judgeResult= getResultPython(
                        problem.getTimeLimit()*2,
                        problem.getMemoryLimit()*2,
                        userSubmit.getContext(),
                        userSubmit.getProblemId(),
                        subId,
                        userSubmit.getCompetitionId()
                );
                if(endTime.isAfter(LocalDateTime.now())&&!loginValidation.isAdmin(userSubmit.getUserId())&&!statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId())&&judgeResult.getProblemStatus().equals(ProblemStatus.ACCEPTED))
                    competitionOperationImpl.acceptSumIncrement(userSubmit.getProblemId(),  userSubmit.getCompetitionId());
                statusAndRank.updateStatus(subId, judgeResult.getMemory(), judgeResult.getTime(), judgeResult.getProblemStatus().getStatus());
                if(endTime.isAfter(LocalDateTime.now()))
                rankOp(userSubmit,judgeResult);
                return judgeResult;
            }
    }



    @Override
    public JudgeResult getResult(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId) {
        //编译
        SandboxCmd sandboxCmd=SandboxCmd.builder()
                .args(List.of("/usr/bin/g++", "a.cc", "-o", "a"))
                .env(List.of("PATH=/usr/bin:/bin"))
                .copyOut(List.of("stdout", "stderr"))
                .copyOutCached(List.of("a"))
                .procLimit(50)
                .memoryLimit(268435456L)
                .cpuLimit(10000000000L)
                .copyIn(Map.of("a.cc",MemoryFile.builder().content(context).build()))
                .build();
        sandboxCmd.setFiles(List.of(
                MemoryFile.builder().content("").build()
                ,Collector.builder().max(10240L).name("stdout").build()
                ,Collector.builder().max(10240L).name("stderr").build()
        ));
        ResponseEntity<SandboxResult[]> resp=getResponse(List.of(sandboxCmd));
        if (resp.getBody() != null && !resp.getBody()[0].getStatus().equals("Accepted")) return JudgeResult.builder()
                .time(0L)
                .memory(0L)
                .problemStatus(ProblemStatus.COMPILEERROR)
                .build();
        String code = resp.getBody()[0].getFileIds().get("a");
        //获取测试数据并封装请求信息

        List<String> outContent=new ArrayList<>();
        List<SandboxCmd> toRun=new ArrayList<>();

        String realPath=basePath+'/'+problemId;
        Path path= Paths.get(realPath);
        try(var stream= Files.list(path)){
            List<Path> paths=stream
                    .filter(p->p.toString().endsWith(".in"))
                    .toList();
            for(Path inPath:paths){
                String fileName = inPath.toFile().getName();
                String prefix=fileName.substring(0,fileName.length()-3);
                String outPath=realPath+'/'+prefix+".out";
                String outString;
                try{
                    outString=Files.readString(Paths.get(outPath));
                }catch(Exception exception){
                    continue;
                }
                outContent.add(outString);
                //塞.in
                SandboxCmd sandboxCmdRun= SandboxCmd.builder()
                        .args(List.of("a"))
                        .env(List.of("PATH=/usr/bin:/bin"))
                        .memoryLimit(memoryLimit)
                        .cpuLimit(timeLimit)
                        .procLimit(1)
                        .copyIn(Map.of("a",PreparedFile.builder().fileId(code).build()))
                        .build();
                sandboxCmdRun.setFiles(List.of(
                        MemoryFile.builder().content(Files.readString(inPath)).build()
                        ,Collector.builder().max(1048576L).name("stdout").build()
                        ,Collector.builder().max(10240L).name("stderr").build()
                ));
                toRun.add(sandboxCmdRun);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ResponseEntity<SandboxResult[]> userOut=getResponse(toRun);
        SandboxResult[] userOutResult=userOut.getBody();
        //删除编译文件
        restTemplate.delete(baseUrl+ "/file/"+code);
        long sumTime=0L;
        for(int i=0;i<toRun.size();i++){
            sumTime+=userOutResult[i].getRunTime();
            if(userOutResult[i].getStatus().equals("Time Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.TIMELIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Memory Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.MEMORYLIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Signalled"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.RUNTIME_ERROR)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            String userString=userOutResult[i].getFiles().get("stdout");
            userString=deleteExtraSpaces(userString);
            if(!outContent.get(i).trim().equals(userString))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.WRONG_ANSWER)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
        }
        return JudgeResult.builder()
                .problemStatus(ProblemStatus.ACCEPTED)
                .memory(userOutResult[0].getMemory())
                .time(sumTime)
                .build();
    }

    @Override
    public JudgeResult getResultPython(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId) {
        List<String> outContent=new ArrayList<>();
        List<SandboxCmd> toRun=new ArrayList<>();

        String realPath=basePath+'/'+problemId;
        Path path= Paths.get(realPath);
        try(var stream= Files.list(path)){
            List<Path> paths=stream
                    .filter(p->p.toString().endsWith(".in"))
                    .toList();
            for(Path inPath:paths){
                String fileName = inPath.toFile().getName();
                String prefix=fileName.substring(0,fileName.length()-3);
                String outPath=realPath+'/'+prefix+".out";
                String outString;
                try{
                    outString=Files.readString(Paths.get(outPath));
                }catch(Exception exception){
                    continue;
                }
                outContent.add(outString);
                //塞.in
                SandboxCmd sandboxCmdRun= SandboxCmd.builder()
                        .args(List.of("python3","/w/main.py"))
                        .env(List.of("PATH=/usr/bin:/bin"))
                        .memoryLimit(memoryLimit)
                        .cpuLimit(timeLimit)
                        .procLimit(15)
                        .copyIn(Map.of("main.py",MemoryFile.builder().content(context).build()))
                        .build();
                sandboxCmdRun.setFiles(List.of(
                        MemoryFile.builder().content(Files.readString(inPath)).build()
                        ,Collector.builder().max(1048576L).name("stdout").build()
                        ,Collector.builder().max(10240L).name("stderr").build()
                ));
                toRun.add(sandboxCmdRun);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ResponseEntity<SandboxResult[]> userOut=getResponse(toRun);
        SandboxResult[] userOutResult=userOut.getBody();
        long sumTime=0L;
        for(int i=0;i<toRun.size();i++){
            sumTime+=userOutResult[i].getRunTime();
            if(userOutResult[i].getStatus().equals("Time Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.TIMELIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Memory Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.MEMORYLIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Nonzero Exit Status"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.NONZEROEXITSTATUS)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            String userString=userOutResult[i].getFiles().get("stdout");
            userString=deleteExtraSpaces(userString);
            if(!outContent.get(i).trim().equals(userString))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.WRONG_ANSWER)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
        }
        return JudgeResult.builder()
                .problemStatus(ProblemStatus.ACCEPTED)
                .memory(userOutResult[0].getMemory())
                .time(sumTime)
                .build();
    }

    @Override
    public JudgeResult getResultJava(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId) {
        //编译
        SandboxCmd sandboxCmd=SandboxCmd.builder()
                .args(List.of("/usr/bin/javac", "-encoding", "UTF-8", "Main.java"))
                .env(List.of("PATH=/usr/bin:/bin"))
                .copyOut(List.of("stdout", "stderr"))
                .copyOutCached(List.of("Main.class"))
                .procLimit(50)
                .memoryLimit(268435456L)
                .cpuLimit(10000000000L)
                .copyIn(Map.of("Main.java",MemoryFile.builder().content(context).build()))
                .build();
        sandboxCmd.setFiles(List.of(
                MemoryFile.builder().content("").build()
                ,Collector.builder().max(10240L).name("stdout").build()
                ,Collector.builder().max(10240L).name("stderr").build()
        ));
        ResponseEntity<SandboxResult[]> resp=getResponse(List.of(sandboxCmd));
        if (resp.getBody() != null && !resp.getBody()[0].getStatus().equals("Accepted")) return JudgeResult.builder()
                .time(0L)
                .memory(0L)
                .problemStatus(ProblemStatus.COMPILEERROR)
                .build();
        String code = resp.getBody()[0].getFileIds().get("Main.class");
        //获取测试数据并封装请求信息

        List<String> outContent=new ArrayList<>();
        List<SandboxCmd> toRun=new ArrayList<>();

        String realPath=basePath+'/'+problemId;
        Path path= Paths.get(realPath);
        try(var stream= Files.list(path)){
            List<Path> paths=stream
                    .filter(p->p.toString().endsWith(".in"))
                    .toList();
            for(Path inPath:paths){
                String fileName = inPath.toFile().getName();
                String prefix=fileName.substring(0,fileName.length()-3);
                String outPath=realPath+'/'+prefix+".out";
                String outString;
                try{
                    outString=Files.readString(Paths.get(outPath));
                }catch(Exception exception){
                    continue;
                }
                outContent.add(outString);
                //塞.in
                SandboxCmd sandboxCmdRun= SandboxCmd.builder()
                        .args(List.of("java","-XX:ThreadStackSize=256k","-XX:+UseSerialGC","-cp","/w","Main"))
                        .env(List.of("PATH=/usr/bin:/bin"))
                        .memoryLimit(memoryLimit)
                        .cpuLimit(timeLimit)
                        .procLimit(80)
                        .copyIn(Map.of("Main.class",PreparedFile.builder().fileId(code).build()))
                        .build();
                sandboxCmdRun.setFiles(List.of(
                        MemoryFile.builder().content(Files.readString(inPath)).build()
                        ,Collector.builder().max(1048576L).name("stdout").build()
                        ,Collector.builder().max(10240L).name("stderr").build()
                ));
                toRun.add(sandboxCmdRun);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ResponseEntity<SandboxResult[]> userOut=getResponse(toRun);
        SandboxResult[] userOutResult=userOut.getBody();
        //删除编译文件
        restTemplate.delete(baseUrl+ "/file/"+code);
        long sumTime=0L;
        for(int i=0;i<toRun.size();i++){
            sumTime+=userOutResult[i].getRunTime();
            if(userOutResult[i].getStatus().equals("Time Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.TIMELIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Memory Limit Exceeded"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.MEMORYLIMITEXCEEDED)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            if(userOutResult[i].getStatus().equals("Nonzero Exit Status"))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.NONZEROEXITSTATUS)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
            String userString=userOutResult[i].getFiles().get("stdout");
            userString=deleteExtraSpaces(userString);
            if(!outContent.get(i).trim().equals(userString))return JudgeResult.builder()
                    .problemStatus(ProblemStatus.WRONG_ANSWER)
                    .memory(userOutResult[i].getMemory())
                    .time(sumTime)
                    .build();
        }
        return JudgeResult.builder()
                .problemStatus(ProblemStatus.ACCEPTED)
                .memory(userOutResult[0].getMemory())
                .time(sumTime)
                .build();
    }

    //发送REST请求
    private ResponseEntity<SandboxResult[]> getResponse(List<SandboxCmd> sandboxCmd) {
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SandboxRequest> httpEntity=new HttpEntity<>(
                SandboxRequest.builder().cmd(sandboxCmd).build()
                ,headers);
        return restTemplate.exchange(
                baseUrl+"/run"
                ,HttpMethod.POST
                ,httpEntity
                ,new ParameterizedTypeReference<SandboxResult[]>() {}
        );
    }
    //处理Rank
    private void rankOp(UserSubmit userSubmit,JudgeResult judgeResult){
        User user=userMapper.getUserById(userSubmit.getUserId());
        if(user.isAdmin()||statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId()) )return;
        if(judgeResult.getProblemStatus().equals(ProblemStatus.ACCEPTED))
        {
            statusAndRank.setAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId(), userSubmit.getSubmittedTime());
            //计算罚时
            CompetitionInformation competitionInformation=competitionMapper.selectCompetitionById(userSubmit.getCompetitionId());
            LocalDateTime startTime=competitionInformation.getStartTime();
            LocalDateTime endTime=userSubmit.getSubmittedTime();
            Duration duration= Duration.between(startTime,endTime);
            long diff=duration.getSeconds();
            Integer times= statusAndRank.getSubmit(userSubmit.getCompetitionId(),  userSubmit.getProblemId(), userSubmit.getUserId());
            diff+= (long) times *60*20;
            statusAndRank.changeScore(userSubmit.getCompetitionId(), userSubmit.getUserId(), (long) (1e11-diff));
        }
        if(!statusAndRank.checkAc(userSubmit.getCompetitionId(), userSubmit.getProblemId(), user.getUserId()))statusAndRank.increaseSubmit(userSubmit.getCompetitionId(), userSubmit.getProblemId(), userSubmit.getUserId());
    }

    //删除换行\n及多余空格
    private String deleteExtraSpaces(String str){
        if(str==null) return "";
        String[] lines = str.split("\\R");
        List<String> validLines = new ArrayList<>(lines.length);
        for (String line : lines) {
            //只删除本行末尾空白，开头不动
            String trimmedLine = line.stripTrailing();
            //过滤空行
            if (!trimmedLine.isEmpty()) {
                validLines.add(trimmedLine);
            }
        }
        return String.join("\n", validLines);
    }
}
