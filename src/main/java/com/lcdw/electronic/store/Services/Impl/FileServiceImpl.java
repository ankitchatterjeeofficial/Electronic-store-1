package com.lcdw.electronic.store.Services.Impl;

import com.lcdw.electronic.store.Exception.BadApiRequest;
import com.lcdw.electronic.store.Services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {

        //orignal filename="abc.png
        String originalFilename = file.getOriginalFilename();
        logger.info("Filename {}",originalFilename);
        String filename= UUID.randomUUID().toString();
        String extension=originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNameWithExtension=filename+extension;
        String fullPathWithFileName=path+fileNameWithExtension;

        logger.info("full image path {}",fullPathWithFileName);
        if(extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")|| extension.equalsIgnoreCase(".jpeg")) {
            //file save
            File folder = new File(path);
            logger.info("file extension is {}",extension);
            if (!folder.exists()) {
                //creating the folder
                folder.mkdirs();
            }

            //upload

            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            logger.info("File Name with Full Path {} ",fullPathWithFileName);
            return fileNameWithExtension;
        }
        else
        {
            throw new BadApiRequest("File With This "+ extension +" Not allowed!!!");
        }
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {

        String fullPath=path+File.separator+name;
        InputStream inputStream=new FileInputStream(fullPath);

        return inputStream;
    }
}
