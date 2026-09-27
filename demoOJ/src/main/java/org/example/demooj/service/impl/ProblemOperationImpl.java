package org.example.demooj.service.impl;

import jakarta.annotation.Resource;
import org.example.demooj.entity.Problem;
import org.example.demooj.mapper.CompetitionProblemSetMapper;
import org.example.demooj.mapper.ProblemMapper;
import org.example.demooj.mapper.UserSubmittedLogMapper;
import org.example.demooj.service.ProblemOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ProblemOperationImpl implements ProblemOperation {

    @Resource
    ProblemMapper problemMapper;
    @Resource
    CompetitionProblemSetMapper competitionProblemSetMapper;
    @Resource
    UserSubmittedLogMapper userSubmittedLogMapper;
    @Value("${file.input}")
    String basePath;

    @Override
    public Integer addProblem(Problem problem) {
        try{
            int temp = problemMapper.addProblem(problem);
            Integer id= problemMapper.getMaxId();
            Path file = Paths.get(basePath,id.toString());
            Files.createDirectories(file);
            return temp;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public List<Problem> selectAllProblems() {
        return problemMapper.selectAllProblem();
    }

    @Override
    public Integer updateProblem(Problem problem) {
        if(problem.getProblemId()==null){
            System.out.println("update-ID不允许为空 默认为1001");
            problem.setProblemId(1001);
        }
        return problemMapper.updateProblemById(problem);
    }

    @Override
    public Integer deleteProblemById(Integer id) {
        if(id==null){
            System.out.println("delete-ID不允许为空 默认为1001");
            id=1001;
        }
        Path file = Paths.get(basePath,id.toString());
        try{
            deleteDir(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        userSubmittedLogMapper.deleteByProblemId(id);
        competitionProblemSetMapper.deleteProblemByProId(id);
        return problemMapper.deleteProblemById(id);
    }

    @Override
    public Problem selectProblemById(Integer id) {
        if(id==null){
            System.out.println("select-ID不允许为空 默认为1001");
            id=1;
        }
        return problemMapper.selectProblemById(id);
    }

    private void deleteDir(Path file)throws IOException {
        Files.walk(file, FileVisitOption.FOLLOW_LINKS)
                .sorted((p1,p2)->p2.compareTo(p1))
                .forEach(
                        path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
    }

    @Override
    public Integer unzipProblem(MultipartFile files, Integer problemId) {
        Path targetDir = Paths.get(basePath, problemId.toString());
        try {
            deleteDir(targetDir);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        if (files.isEmpty()) {
            return -3;
        }
        try (InputStream is = files.getInputStream(); ZipInputStream zipInputStream = new ZipInputStream(is, StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.contains("..")) {
                    zipInputStream.closeEntry();
                    continue;
                }
                //丢弃路径，只拿文件名
                String fileName = Paths.get(entryName).getFileName().toString();
                //不生成任何子目录
                if (entry.isDirectory()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                Path outPath = targetDir.resolve(fileName);

                try (OutputStream os = Files.newOutputStream(outPath)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -4;
        }
        return 1;
    }
}
