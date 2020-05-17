package com.example.puzzlio;

import android.graphics.Bitmap;

import com.example.puzzlio.MainActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.resize;

public class ImageProcessing{

    private ScanTest scanTest;
    private Mat grayMat, largestMat;
    private Bitmap bitmap, finalbmp;
    private double crosswordContourIdx;

    public ImageProcessing(ScanTest scanTest){
        this.scanTest = scanTest;
    }

    public void img(){

        Mat m = new Mat();

        Utils.bitmapToMat(bitmap, m);

        //set image to greyscale
        Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2GRAY);
        //invert white and blacks


        //create copy of greyscale image
        grayMat = m.clone();
        Mat cannyEdges = new Mat();
        Mat lines= new Mat();

        //dilation kernel
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        Imgproc.dilate(grayMat, grayMat, kernel);

        //find line detection
        Imgproc.Canny(grayMat, cannyEdges, 10, 200);
        Imgproc.dilate(cannyEdges, cannyEdges, kernel);

        //merge line widths then thin them

        Imgproc.erode(cannyEdges, cannyEdges, kernel2);

        //firstcontours
//        Core.merge();

        Mat cropped = cropToLargestContour(cannyEdges);
//
        extractGrids(cropped);
        //second
//        Mat cropped2 = cropToSmallestContour(cropped);
//
//        Mat cropTest = cropTest(cropped);

//        Imgproc.HoughLinesP(cropped, lines, 1, Math.PI/180, 50);
//
//        Mat houghLines = new Mat();
//        houghLines.create(cropped.rows(), cropped.cols(), CvType.CV_8UC1);
//
//
//        for (int i = 0; i < lines.cols(); i++)
//        {
//            double rho = lines.get(i, 0)[0],
//                    theta = lines.get(i, 0)[1];
//            double a = Math.cos(theta), b = Math.sin(theta);
//            double x0 = a*rho, y0 = b*rho;
//            Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
//            Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
//
//
//            Imgproc.line(houghLines, pt1, pt2, new Scalar(0, 0, 255), 3);
//        }

//       Toast.makeText(this, "" + lines.cols(), Toast.LENGTH_SHORT).show();

        //bitmap creation and matToBitmap need to use same mat, use cropped for greyscale cropped image, without extra processing

        //set the bitmap to display image
        scanTest.setBitmaps(cropped);
    }

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
//            System.out.println("contours " + contourArea);
            if (contourArea > largest_area) {
                largest_area = contourArea;
                largest_contour_index = contourIdx;
            }
        }
//        Imgproc.drawContours(mat, contours, largest_contour_index, new Scalar(0, 255, 255), 3);
        crosswordContourIdx = Imgproc.contourArea(contours.get(largest_contour_index));
        Rect rect = Imgproc.boundingRect(contours.get(largest_contour_index));
        Mat crop =  mat.submat(rect); //use m.submat for image without lines
        largestMat = crop;
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


//    public Mat cropTest(Mat mat){
//        Mat hierachy = new Mat();
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(mat, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);
//        List<MatOfPoint> filteredContours = new ArrayList<>();
//        double sizeLower = (crosswordContourIdx / 1000) * 8;
//        double sizeHigher = (crosswordContourIdx /1000) * 12;
//
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            if(Imgproc.contourArea(contours.get(contourIdx)) < sizeLower || Imgproc.contourArea(contours.get(contourIdx)) > sizeHigher) {
//                continue;
//            }else{
//                filteredContours.add(contours.get(contourIdx));
//            }
//        }
//
//        System.out.println("lower size = " + sizeLower + " " + "upper = " + " " + sizeHigher + " dims");
//        Rect rect = Imgproc.boundingRect(filteredContours.get(10));
//        System.out.println("largest contour:" + crosswordContourIdx + " " + "dims: " + Imgproc.contourArea(filteredContours.get(9)) + " " + "length of list: " + filteredContours.size());
//        //110
//        //13466
//        Mat crop =  mat.submat(rect); //use m.submat for image without lines
//        extractGrids(largestMat);
//        return crop;
//    }

    public void extractGrids(Mat grid){
        Mat g;
        System.out.println("dims " + grid.size());
        int x = 0, y = 0;
//        Rect rect = new Rect(x,  y, crop.width(), crop.height());
        List<Mat> grids = new ArrayList<>();

        int width = grid.width() / 9;
        int height = grid.height() / 9;
        Rect rect = new Rect(x, y, width, height);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));

        for(int i = 0; i < 9; i++) {
            rect.x = 0;
            for (int o = 0; o < 9; o++) {
                g = grid.submat(rect);
                Mat m = g.submat(new Rect(18 , 18, width -33, height -33));
//                Core.bitwise_not(m, m);
//                resize(m, m, new Size(grid.width(), grid.height()), INTER_CUBIC);
                grids.add(m);

                rect.x += width;
            }

            rect.y += height;
        }


//        int yDif = crop.height();
//            do {
//                if(rect.x + crop.width() >= grid.width()){
//                    int temp;
//                    temp = ((rect.x + crop.width() - grid.width()));
//                    rect.width = crop.width() - temp;
//                    m = grid.submat(rect);
//                    grids.add(m);
//
//                    rect.width = crop.width();
//
//                    if(rect.y + crop.height() >= grid.height()){
//                        yDif = ((rect.y + crop.height() - grid.height()));
//                        rect.height = crop.height() - yDif;
//                    }else {
//                        rect.y += crop.height();
//                        rect.x = 0;
//                    }
//                }else{
//                    m = grid.submat(rect);
//                    grids.add(m);
//                    rect.x += crop.width();
//                }
//
//                if(rect.x + crop.width() >= grid.height() && rect.y + crop.height() >= grid.height())
//                    break;
//
//
//            }
//            while (yDif == crop.height());

            scanTest.writeMats(grids);


    }


    public Mat drawContours(Mat mat){
        Mat hierachy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mat, contours, hierachy, RETR_TREE, CHAIN_APPROX_SIMPLE);



        for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(mat, contours, i, new Scalar(255, 0, 255), 3);
        }

        return mat;
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
