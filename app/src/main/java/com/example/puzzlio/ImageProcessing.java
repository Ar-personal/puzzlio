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

    public ImageProcessing(ScanTest scanTest){
        this.scanTest = scanTest;
    }

    public void img(){
        Mat kernelD = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(11, 11));
        Mat m = new Mat();
        Mat adaptive = new Mat();

        Utils.bitmapToMat(bitmap, m);

        Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(m, m, new Size(11, 11), 0);
        Bitmap gaussian = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, gaussian);
        scanTest.writeImageToStorage(gaussian);


        Imgproc.adaptiveThreshold(m, adaptive, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 55, 5);
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




    public Mat cropToSmallestContour(Mat mat) {
        Mat hierachy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        double smallest_area = 0;
        int smallest_contour_index = 0;
        int minContourSize = 1;

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (contourArea < smallest_area && contourArea > minContourSize) {
                smallest_area = contourArea;
                smallest_contour_index = contourIdx;
            }
        }

        Rect rect = Imgproc.boundingRect(contours.get(smallest_contour_index));
        Mat crop =  mat.submat(rect); //use m.submat for image without lines

        return crop;
    }

    public void extractGrids(Mat grid){
        //calculate the dims based on the puzzle size, should use passed var

        List gridList = new ArrayList<Mat>();
//        Mat[][] grids = new Mat[9][9];
        ArrayList<Mat> grids = new ArrayList();
        Mat hierachy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(grid, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        List<Bitmap> bitmapList = new ArrayList<>();





        //loop through all contours and calculate area of reasonable sizes
        double contavg = 0, threshhold = 0, contourArea = 0, count = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
//            Mat m = grid.submat(rect);
//            Bitmap b1 = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(m, b1);
            contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (contourArea > 55000 && contourArea < 80000) {
//                Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
//                Mat trimmed = grid.submat(rect);
//                Bitmap b = Bitmap.createBitmap(trimmed.cols(), trimmed.rows(), Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(trimmed, b);
//                bitmapList.add(b);
            }else{
                contours.remove(contourIdx);
            }
        }

        //margin of error based on the average
//        contavg = contavg / count;
//        threshhold = (contavg / 100) * 9;
//
//        //choose all contours within threshold
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            contourArea = Imgproc.contourArea(contours.get(contourIdx));
//            Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
//            Mat m = grid.submat(rect);
//            Bitmap b1 = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(m, b1);
//            if (contourArea < contavg + threshhold && contourArea > contavg - threshhold) {
//                System.out.println("contour width: " + contours.get(contourIdx).width() + " contour height: " + contours.get(contourIdx).height() + " contour area: " + contourArea);
//                System.out.println("contours " + contourArea);
//
//
//                trimmedContours.add(contours.get(contourIdx));
//
//            }
//        }

        //arrange contours by left to right and top to bottom
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


        //trim and reprocess the contours
        for(MatOfPoint m : contours){
            if(m == null){
                continue;
            }
                try {
                    //crop manually?
                    Rect rect = Imgproc.boundingRect(m);
                    Rect croppedGrid = new Rect(rect.x + 14, rect.y + 35, rect.width - 50, rect.height -60);
                    Mat trimmed = grid.submat(croppedGrid);

                    //erode then crop? a good erode may mean no manual cropping needed
//                    Mat kernelE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(13, 13));
//                    erode(trimmed, trimmed, kernelE);
//                    Bitmap b = Bitmap.createBitmap(trimmed.cols(), trimmed.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(trimmed, b);
//                    scanTest.writeImageToStorage(b);

                    grids.add(trimmed);
                }catch (IndexOutOfBoundsException o){
                    o.printStackTrace();
                }
            }

        scanTest.setGridSize(contours.size());
        scanTest.writeMats(grids);

    }

    public Mat deskew(Mat src, double angle) {
        Point center = new Point(src.width()/2, src.height()/2);
        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        //1.0 means 100 % scale
        Size size = new Size(src.width(), src.height());
        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return src;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getFinalbmp() {
        return finalbmp;
    }

    public void setFinalbmp(Bitmap finalbmp) {
        this.finalbmp = finalbmp;
    }



}
