package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.repository.BinaryType;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class FileExtractorTest {

    private static final boolean OVERWRITE_EXISTING_FILES = true;
    private static final boolean DO_NOT_OVERWRITE_EXISTING_FILES = false;
    private static final String VALID_HASH = "add36bb347a987b56e533c2034fd37b1";
    private final URL test7ZipFile = this.getClass().getResource("/jetty/files/download.7z");
    private final URL testZipFile = this.getClass().getResource("/jetty/files/download.zip");
    private final URL testTarGZFile = this.getClass().getResource("/jetty/files/download.tar.gz");
    private final URL testTarBZ2File = this.getClass().getResource("/jetty/files/download.tar.bz2");
    private static File phantomJSTestFile;
    private static String tempDir;

    @Before
    public void initialiseFile() {
        String tempDirectory = System.getProperty("java.io.tmpdir");
        if (tempDirectory.endsWith("/")) {
            tempDir = System.getProperty("java.io.tmpdir") + java.util.UUID.randomUUID();
        } else {
            tempDir = System.getProperty("java.io.tmpdir") + File.separator + java.util.UUID.randomUUID();
        }
        phantomJSTestFile = new File(tempDir + File.separator + "phantomjs");
    }

    @After
    public void cleanUp() {
        if (phantomJSTestFile.exists()) {
            assertThat(phantomJSTestFile.delete(), is(equalTo(true)));
        }
    }

    @Test
    public void successfullyExtractFileFromZipArchive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        String extractedFilePath = fileExtractor.unzipFile(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash,
                is(equalTo(VALID_HASH)));
        assertThat(extractedFilePath,
                is(equalTo(tempDir + "/phantomjs")));
    }

    @Test
    public void overwriteExistingFileWhenExtractingFromZip() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        long lastModified = phantomJSTestFile.lastModified();
        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        fileExtractor.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);

        assertThat(phantomJSTestFile.lastModified(), is(not(equalTo(lastModified))));
    }

    @Test
    public void doNotOverwriteExistingFileWhenExtractingFromZip() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        long lastModified = phantomJSTestFile.lastModified();
        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        fileExtractor.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);

        assertThat(phantomJSTestFile.lastModified(), is(equalTo(lastModified)));
    }

    @Test
    public void successfullyExtractFileFromTarGZipArchive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        String extractedFilePath = fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash,
                is(equalTo(VALID_HASH)));
        assertThat(extractedFilePath,
                is(equalTo(tempDir + "/phantomjs")));
    }

    @Test
    public void overwriteExistingFileWhenExtractingFromTar() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        long lastModified = phantomJSTestFile.lastModified();
        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);

        assertThat(phantomJSTestFile.lastModified(), is(not(equalTo(lastModified))));
    }

    @Test
    public void doNotOverwriteExistingFileWhenExtractingFromTar() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        long lastModified = phantomJSTestFile.lastModified();
        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);

        assertThat(phantomJSTestFile.lastModified(), is(equalTo(lastModified)));
    }

    @Test
    public void successfullyExtractFileFromTarBZip2Archive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testTarBZ2File.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash, is(equalTo(VALID_HASH)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUntarAnArchiveThatIsNotATarFile() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(test7ZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromZipArchive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash, is(equalTo(VALID_HASH)));
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarGZipArchive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash, is(equalTo(VALID_HASH)));
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarBZip2Archive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(testTarBZ2File.getFile()), tempDir, BinaryType.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);
        String downloadedFileHash = DigestUtils.md5Hex(fileToCheck);
        fileToCheck.close();

        assertThat(downloadedFileHash, is(equalTo(VALID_HASH)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryAndExtractFromAnUnsupportedArchive() throws Exception {
        FileExtractor fileExtractor = new FileExtractor(DO_NOT_OVERWRITE_EXISTING_FILES);
        fileExtractor.extractFileFromArchive(new File(test7ZipFile.getFile()), tempDir, BinaryType.PHANTOMJS);
    }
}
