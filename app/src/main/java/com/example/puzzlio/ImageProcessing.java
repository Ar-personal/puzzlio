package com.example.puzzlio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;

import com.example.puzzlio.MainActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.accumulateSquare;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.resize;

public class ImageProcessing{

    private ScanTest scanTest;
    private Bitmap bitmap, finalbmp;
    private List<Bitmap> bitmapList, removedBitmapList;

    public ImageProcessing(ScanTest scanTest){
        this.scanTest = scanTest;
    }

    public void img(){
        Mat kernelD = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6));
        Mat m = new Mat();
        Mat adaptive = new Mat();

        Utils.bitmapToMat(bitmap, m);

        Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(m, m, new Size(15, 15), 0);
        Bitmap gaussian = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, gaussian);
        scanTest.writeImageToStorage(gaussian);


        Imgproc.adaptiveThreshold(m, adaptive, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 99, 7);
        Bitmap adaptiveThreshold = Bitmap.createBitmap(adaptive.cols(), adaptive.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(adaptive, adaptiveThreshold);
        scanTest.writeImageToStorage(adaptiveThreshold);

        //invert white and blacks
        Core.bitwise_not(adaptive, adaptive);
        Bitmap bitwiseNot = Bitmap.createBitmap(adaptive.cols(), adaptive.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(adaptive, bitwiseNot);
        scanTest.writeImageToStorage(bitwiseNot);
        //repair lines

        Imgproc.dilate(adaptive, adaptive, kernelD);
        Bitmap dilation = Bitmap.createBitmap(adaptive.cols(), adaptive.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(adaptive, dilation);
        scanTest.writeImageToStorage(dilation);

//
//        Mat result = computeSkew(adaptive);
//        Bitmap bitwiseNot = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(result, bitwiseNot);

        Mat cropped = cropToLargestContour(adaptive);
        Bitmap largestContour = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cropped, largestContour);
        scanTest.writeImageToStorage(largestContour);

        //find line detection
//        Imgproc.Canny(adaptive, cannyEdges, 10, 200);
//        Imgproc.dilate(cannyEdges, cannyEdges, kernel5);


//        erode(cropped, cropped, kernelE);
//        Bitmap b5 = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(cropped, b5);
//        scanTest.writeImageToStorage(b5);

        Rect r = Imgproc.boundingRect(cropped);

//        Point topLeft = new Point(r.x, r.y), topRight = new Point(r.x + r.width, r.y), bottomLeft = new Point(r.x, r.y + r.height), bottomRight = new Point(r.x + r.width, r.y + r.height);
//        List<Point> source = new ArrayList<Point>();
//        source.add(topLeft);
//        source.add(topRight);
//        source.add(bottomRight);
//        source.add(bottomLeft);
//        Mat startM = Converters.vector_Point2f_to_Mat(source);
//
//        double m0 = Math.max(distance_between(topLeft, topRight), distance_between(topRight, bottomRight));
//        double m1 = Math.max(distance_between(bottomLeft, bottomRight), distance_between(topLeft, bottomLeft));
//        double side = Math.max(m0, m1);
//
//        Point ocvPOut1 = new Point(0, 0);
//        Point ocvPOut2 = new Point(side, 0);
//        Point ocvPOut3 = new Point(side, side);
//        Point ocvPOut4 = new Point(0, side);
//        List<Point> output = new ArrayList<Point>();
//        output.add(ocvPOut1);
//        output.add(ocvPOut2);
//        output.add(ocvPOut3);
//        output.add(ocvPOut4);
//        Mat endM = Converters.vector_Point2f_to_Mat(output);
//
//
//
//        Rect newRect = new Rect(0, 0, (int) side, (int) side);
//
//        Mat src = new Mat(4, 1, CvType.CV_32FC2);
//        src.put((int) topLeft.x, (int) topLeft.y, topRight.x, topRight.y, bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);
//
//        Mat dst = new Mat(4, 1, CvType.CV_32FC2);
//        dst.put(0,0, 0, cropped.width(), cropped.height(), cropped.width(), cropped.height(), 0);
//        Mat outputMat = new Mat((int) side, (int) side, CvType.CV_8UC4);
//        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
//        Imgproc.warpPerspective(cropped, outputMat, perspectiveTransform, new Size(side, side), INTER_CUBIC);
////
//        Bitmap b6 = Bitmap.createBitmap((int) side, (int) side, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(outputMat, b6);
//        scanTest.writeImageToStorage(b6);
//
//        Bitmap b7 = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(cropped, b7);
//
//        Bitmap b8 = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(perspectiveTransform, b8);
        extractGrids(cropped);

        //set the bitmap to display image
        scanTest.setBitmaps(cropped);
    }

    public double distance_between(Point p1, Point p2){
        double a = p2.x - p1.x;
        double b = p2.y - p2.y;
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

//    public Mat computeSkew(Mat mat) {
//        //Load this image in grayscale
//        Mat img = mat;
//
//        Bitmap b1 = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(img, b1);
//
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kerne);
//
//        //We can now perform our erosion, we must declare our rectangle-shaped structuring element and call the erode function
//        Imgproc.erode(img, img, element);
//
//        //Find all white pixels
//        Mat wLocMat = Mat.zeros(img.size(),img.type());
//        Core.findNonZero(img, wLocMat);
//
//        //Create an empty Mat and pass it to the function
//        MatOfPoint matOfPoint = new MatOfPoint( wLocMat );
//
//        //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
//        MatOfPoint2f mat2f = new MatOfPoint2f();
//        matOfPoint.convertTo(mat2f, CvType.CV_32FC2);
//
//        //Get rotated rect of white pixels
//        RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );
//
//        Point[] vertices = new Point[4];
//        rotatedRect.points(vertices);
//        List<MatOfPoint> boxContours = new ArrayList<>();
//        boxContours.add(new MatOfPoint(vertices));
//        Imgproc.drawContours( img, boxContours, 0, new Scalar(128, 128, 128), -1);
//
//        double resultAngle = rotatedRect.angle;
//        if (rotatedRect.size.width > rotatedRect.size.height)
//        {
//            rotatedRect.angle += 90.f;
//        }
//
//        //Or
//        //rotatedRect.angle = rotatedRect.angle < -45 ? rotatedRect.angle + 90.f : rotatedRect.angle;
//
//        Mat result = deskew(mat, rotatedRect.angle );
//        return result;
//
//    }

    public Mat cropToLargestContour(Mat mat){
        //find the largest grid in the images and then crops to that grid, removing clutter around puzzle
        Mat hierachy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        System.out.println("contours " + contours.size());

        double largest_area =0;
        int largest_contour_index = 0;

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (contourArea > largest_area) {
                largest_area = contourArea;
                largest_contour_index = contourIdx;
            }
        }
        Rect rect = Imgproc.boundingRect(contours.get(largest_contour_index));
        Mat crop =  mat.submat(rect); //use m.submat for image without lines

        return crop;
    }


//    public Mat cropToSmallestContour(Mat mat) {
//        Mat hierachy = new Mat();
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(mat, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);
//
//        double smallest_area = 0;
//        int smallest_contour_index = 0;
//        int minContourSize = 1;
//
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
//            if (contourArea < smallest_area && contourArea > minContourSize) {
//                smallest_area = contourArea;
//                smallest_contour_index = contourIdx;
//            }
//        }
//
//        Rect rect = Imgproc.boundingRect(contours.get(smallest_contour_index));
//        Mat crop =  mat.submat(rect); //use m.submat for image without lines
//
//        return crop;
//    }

    public void trimContoursToThresholds(List<MatOfPoint> contoursCopy, Mat grid, double lower, double higher){
        double contourArea;
        bitmapList = new ArrayList<>();
        removedBitmapList = new ArrayList<>();
        for (int contourIdx = 0; contourIdx < contoursCopy.size(); contourIdx++) {
            contourArea = Imgproc.contourArea(contoursCopy.get(contourIdx));
            if (contourArea > lower && contourArea < higher) {
                Rect rect = Imgproc.boundingRect(contoursCopy.get(contourIdx));
                Mat trimmed = grid.submat(rect);
                Bitmap b = Bitmap.createBitmap(trimmed.cols(), trimmed.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(trimmed, b);
                bitmapList.add(b);
            }else{
                Rect rect = Imgproc.boundingRect(contoursCopy.get(contourIdx));
                Mat trimmed = grid.submat(rect);
                Bitmap b = Bitmap.createBitmap(trimmed.cols(), trimmed.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(trimmed, b);
                removedBitmapList.add(b);
                contoursCopy.remove(contourIdx);
            }
        }
    }

    public void extractGrids(Mat grid){

//        Mat[][] grids = new Mat[9][9];
        ArrayList<Mat> grids = new ArrayList();
        Mat hierachy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(grid, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> contoursCopy = new ArrayList<>(contours);
        bitmapList = new ArrayList<>();
        removedBitmapList = new ArrayList<>();
        boolean maxed = false;

        double contAvg = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            contAvg += Imgproc.contourArea(contours.get(contourIdx));
        }

        contAvg = contAvg / contours.size();
        double lower = contAvg -= 20000;
        double higher = contAvg += 2000;
        int iterations = 0;
        //loop through all contours and calculate area of reasonable sizes

        while(!maxed){
            if(lower < 50000 || bitmapList.size() >= 81) {
                break;
            }
            contoursCopy = new ArrayList<>(contours);
            if(bitmapList.size() < 81) {
                trimContoursToThresholds(contoursCopy, grid, lower, higher);
                lower -= 1000;
                higher += 1000;
                System.out.println(iterations);
                iterations++;
            }else{
                maxed = true;
            }
        }

        contours = new ArrayList<>(contoursCopy);

        //arrange contours by left to right and top to bottom
        try {
            Collections.sort(contours, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint o1, MatOfPoint o2) {
                    Rect rect1 = Imgproc.boundingRect(o1);
                    Rect rect2 = Imgproc.boundingRect(o2);
                    int result = 0;
                    double total = rect1.tl().y/rect2.tl().y;
                    if (total>=0.9 && total<=1.4 ){
                        result = Double.compare(rect1.tl().x, rect2.tl().x);
                    }
                    return result;
                }
            });


            Collections.sort(contours, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint o1, MatOfPoint o2) {
                    Rect rect1 = Imgproc.boundingRect(o1);
                    Rect rect2 = Imgproc.boundingRect(o2);
                    int result = Double.compare(rect1.tl().y, rect2.tl().y);
                    return result;
                }
            } );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("contours length: "+ contours.size());
        }

        //trim and reprocess the contours
        for(MatOfPoint m : contours){
                    //crop manually?
                    Rect rect = Imgproc.boundingRect(m);
//                    Rect croppedGrid = new Rect(rect.x + 5, rect.y + 10, rect.width - 5, rect.height -10);
                    Mat trimmed = grid.submat(rect);
                    Mat border = new Mat();

                    Mat kernelE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7));
                    erode(trimmed, trimmed, kernelE);

                    Imgproc.rectangle(trimmed, new Point(0, 0), new Point(0 + trimmed.width(), 0 + trimmed.height()), new Scalar(0, 0, 0), 30);


//                     Core.copyMakeBorder(trimmed, border, 50, 50, 50, 50, Core.BORDER_ISOLATED);
//                    Mat cropped = cropToSmallestContour(trimmed);

                    //erode then crop? a good erode may mean no manual cropping needed
                    Bitmap b = Bitmap.createBitmap(trimmed.cols(), trimmed.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(trimmed, b);
                    scanTest.writeImageToStorage(b);

                    grids.add(trimmed);
            }

        scanTest.setGridSize(81);
        scanTest.writeMats(grids);

    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
