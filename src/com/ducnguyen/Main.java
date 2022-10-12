package com.ducnguyen;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path;
        if (args.length == 0) {
            System.out.print("Enter start path: ");
            Scanner scanner = new Scanner(System.in);
            path = Paths.get(scanner.nextLine());
        } else {
            path = Paths.get(args[0]);
        }
        FileVisitor fv = new FileVisitor();
        Files.walkFileTree(path, fv);
        System.out.println("Total scanned " + fv.counted + " items.");
    }

    public static class FileVisitor extends SimpleFileVisitor<Path> {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/._*");
        PathMatcher dsStoreMatcher = FileSystems.getDefault().getPathMatcher("glob:**/.DS_Store");
        PathMatcher hiddenDSStoreMatcher = FileSystems.getDefault().getPathMatcher("glob:**/._.DS_Store");
        PathMatcher thumbDBeMatcher = FileSystems.getDefault().getPathMatcher("glob:**/Thumbs.db");
        long counted = 0;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if ((matcher.matches(file) &&
                    attrs.isRegularFile() &&
                    Files.isHidden(file) &&
                    Files.exists(file.resolveSibling(file.getFileName().toString().substring(2))))
                    || dsStoreMatcher.matches(file)
                    || hiddenDSStoreMatcher.matches(file)
                    || thumbDBeMatcher.matches(file)) {
                System.out.println(" -- remove: " + file.toAbsolutePath());
                Files.delete(file);
            }
            counted++;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.println("ERROR: " + exc);
            return FileVisitResult.CONTINUE;
        }
    }
}
