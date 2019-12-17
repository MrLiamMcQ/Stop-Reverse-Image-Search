import java.awt.Color;



import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Rectangle;

import net.miginfocom.swing.MigLayout;

import java.io.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JEditorPane;
import javax.swing.DropMode;
import javax.swing.border.BevelBorder;

import java.awt.Font;

//https://yandex.com/
public class StopReverseSearch {
	
	
	private BufferedImage loadImg(String location,boolean local) {
		BufferedImage img = null;
		try {
			if(local)
				img = ImageIO.read(getClass().getResource(location));
			else
				img = ImageIO.read(new File(location));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		JLabel gh = new JLabel("");
		
		return img;
	}
	
	private int clampColor(int inCol){
		if(inCol > 255)
			inCol = 255;
		else if(inCol < 0)
			inCol = 0;
		return inCol;
	}
	
	private BufferedImage changeColour(BufferedImage img,int R,int G,int B){
		
		for (int x = 0; x < img.getWidth(); x++) {
		    for (int y = 0; y < img.getHeight(); y++) {
		    	
		    	Color cul = new Color(img.getRGB(x, y));
		    	
		    	int newRed = clampColor(cul.getRed()+R);
		    	int newBlue = clampColor(cul.getBlue()+B);
		    	int newGreen = clampColor(cul.getGreen()+G);
		    		
		    	Color editedCul = new Color(newRed,newGreen,newBlue);
		        img.setRGB(x, y, editedCul.getRGB());
		    }
		}
		
		return img;
	}
	
    BufferedImage flipHorazontal(BufferedImage sprite){
            BufferedImage img = new BufferedImage(sprite.getWidth(),sprite.getHeight(),BufferedImage.TYPE_INT_RGB);
            for(int xx = sprite.getWidth()-1;xx>0;xx--){
                for(int yy = 0;yy < sprite.getHeight();yy++){
                    img.setRGB(sprite.getWidth()-xx, yy, sprite.getRGB(xx, yy));
                }
            }
        return img;
    }
	
	private BufferedImage changeBrightness(BufferedImage img, boolean makeBrighter){
		
		for (int x = 0; x < img.getWidth(); x++) {
		    for (int y = 0; y < img.getHeight(); y++) {
		    	Color cul = new Color(img.getRGB(x, y));
		    	if(makeBrighter)
		    		img.setRGB(x, y, cul.brighter().getRGB());
		    	else
		    		img.setRGB(x, y, cul.darker().getRGB());
		    }
		}
		
		return img;
	}

	private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
	      BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return dest; 
	}
	
	private int textToRgbEditingValue(String value){
		int retVal;
		try {
			retVal = Integer.parseInt(value);
			if(retVal >= -255 && retVal <= 255)
				return retVal;
			else throw new NumberFormatException("");
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Invalid: "+value, "incorect RGB value set", JOptionPane.INFORMATION_MESSAGE);
			retVal = 0xDEAD;
		}
		return retVal;
	}
	
	private JFrame frmStopReverseImage;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StopReverseSearch window = new StopReverseSearch();
					window.frmStopReverseImage.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StopReverseSearch() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	public JLabel IMG = new JLabel("");
	
	public BufferedImage currentImg = null;
	JSlider slider1 = new JSlider();
	JFormattedTextField redT = new JFormattedTextField();
	JFormattedTextField greenT = new JFormattedTextField();
	JFormattedTextField blueT = new JFormattedTextField();
	public double imgRotation = 0;
	
	private void drawImg(){
		if(imgRotation != 0){
			AffineTransform transform = AffineTransform.getRotateInstance(imgRotation, currentImg.getWidth()/2, currentImg.getHeight()/2);
			AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage newBuff = op.filter(currentImg, null);
			ImageIcon img2 = new ImageIcon(newBuff.getScaledInstance(currentImg.getWidth(), currentImg.getHeight(), Image.SCALE_DEFAULT));
			IMG.setIcon(img2);
		}
		else{
			ImageIcon img2 = new ImageIcon(currentImg);
			IMG.setIcon(img2);
		}
	}
	
	private void autoCropRotatedImg(){
		float degreesRotated = slider1.getValue()/10;
		
		AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(degreesRotated), currentImg.getWidth()/2, currentImg.getHeight()/2);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		
		BufferedImage target = new BufferedImage((int)(currentImg.getWidth()*1), (int)(currentImg.getHeight()*1), BufferedImage.TYPE_INT_RGB);
		
		currentImg = op.filter(currentImg, null);
		
		Graphics2D g = (Graphics2D) target.getGraphics();
		g.drawImage(currentImg, 0, 0, null);
		
		int newX = currentImg.getWidth()-target.getWidth();
		int newY = currentImg.getHeight()-target.getHeight();
		target = cropImage(target, new Rectangle(newX,newY,target.getWidth()-newX-newX,target.getHeight()-newY-newY ));
		currentImg = target;
		
		slider1.setValue(0);
	}
	
	private void initialize() {
		frmStopReverseImage = new JFrame();
		frmStopReverseImage.setResizable(false);
		frmStopReverseImage.setTitle("stop reverse image search");
		frmStopReverseImage.setLocationRelativeTo(null);
		frmStopReverseImage.setBounds(100, 100, 944, 684);
		frmStopReverseImage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{282, 282, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{485, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		frmStopReverseImage.getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_IMG = new GridBagConstraints();
		gbc_IMG.gridwidth = 7;
		gbc_IMG.fill = GridBagConstraints.BOTH;
		gbc_IMG.insets = new Insets(0, 0, 0, 5);
		gbc_IMG.gridx = 0;
		gbc_IMG.gridy = 0;
		IMG.setHorizontalAlignment(SwingConstants.CENTER);
		frmStopReverseImage.getContentPane().add(IMG, gbc_IMG);
		
		currentImg = null;//loadImg("/important2.jpg",true);
		//drawImg();

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 7;
		gbc_panel.gridy = 0;
		frmStopReverseImage.getContentPane().add(panel, gbc_panel);
		panel.setLayout(new MigLayout("", "[63px,grow][63px,grow][63px,grow]", "[][][][][][][][][][][][][][][][][][][][][200px,grow,fill]"));//"[23px][][grow][grow][][][][][][][][][][][][][grow][grow]"
		
		JButton lighter = new JButton("lighter");
		panel.add(lighter, "cell 0 0 3 1,growx,aligny top");
		lighter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentImg = changeBrightness(currentImg,true);
				drawImg();
			}
		});
		
		JButton darker = new JButton("darker");
		panel.add(darker, "cell 0 1 3 1,growx,aligny top");
		
		darker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentImg = changeBrightness(currentImg,false);
				drawImg();
			}
		});
		slider1.setMinimum(-180);
		slider1.setMaximum(180);
		slider1.setValue(0);
		
		slider1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				imgRotation = Math.toRadians(slider1.getValue()/10);
				drawImg();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Rotation");
		panel.add(lblNewLabel, "cell 0 3 3 1,alignx center");
		
		panel.add(slider1, "cell 0 4 3 1,growx");
		
		JButton crop = new JButton("auto crop");
		crop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoCropRotatedImg();
				drawImg();
			}
		});
		panel.add(crop, "cell 0 5 3 1,grow");
		
		JButton btnNewButton = new JButton("flip");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentImg = flipHorazontal(currentImg);
				drawImg();
			}
		});
		panel.add(btnNewButton, "cell 0 7 3 1,growx");
		
		JButton smoothEdge = new JButton("smooth edges");
		smoothEdge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				for(int i=0;i<20;i++){
					EdgeDetector edgeDetector = new EdgeDetector();//BufferedImage result
					currentImg = edgeDetector.detect(currentImg);
					drawImg();
				}
				
			}
		});
		panel.add(smoothEdge, "cell 0 8 3 1,growx");
		
		JButton border = new JButton("add noise border");
		border.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage borderdImg = new BufferedImage((int)(currentImg.getWidth()*1.1), (int)(currentImg.getHeight()*1.1), BufferedImage.TYPE_INT_RGB);
				Random rand = new Random();
				
				for (int x = 0; x < borderdImg.getWidth(); x++) {
				    for (int y = 0; y < borderdImg.getHeight(); y++) {
				    	Color cul = new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
				    	borderdImg.setRGB(x, y, cul.getRGB());
				    }
				}
				
				borderdImg.getGraphics().drawImage(currentImg, (int)(currentImg.getWidth()*0.05), (int)(currentImg.getHeight()*0.05), null);;
				
				currentImg = borderdImg;
				drawImg();
				frmStopReverseImage.pack();
			}
		});
		panel.add(border, "cell 0 9 3 1,growx");
		
		JLabel lblRed = new JLabel("red");
		lblRed.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblRed, "cell 0 11,alignx center,aligny center");
		
		JLabel lblGreen = new JLabel("green");
		panel.add(lblGreen, "cell 1 11,alignx center");
		
		JLabel lblBlue = new JLabel("blue");
		panel.add(lblBlue, "cell 2 11,alignx center");
		
		redT.setText("5");
		panel.add(redT, "cell 0 12,growx");
		
		greenT.setText("-1");
		panel.add(greenT, "cell 1 12,growx");
		
		blueT.setText("5");
		panel.add(blueT, "cell 2 12,growx");
		
		JButton newColor = new JButton("apply color edit");
		newColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int blue = textToRgbEditingValue(blueT.getText());
				int red = textToRgbEditingValue(redT.getText());
				int green = textToRgbEditingValue(greenT.getText());
				
				if(blue == 0xDEAD|| red == 0xDEAD|| green == 0xDEAD)
					return;
				
				currentImg = changeColour(currentImg,red,green,blue);
				drawImg();
			}
		});
		panel.add(newColor, "cell 0 13 3 1,growx");
		
		JButton wFile = new JButton("write to file");
		wFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				File outputfile = new File("image.png");
				try {
					AffineTransform transform = AffineTransform.getRotateInstance(imgRotation, currentImg.getWidth()/2, currentImg.getHeight()/2);
					AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
					BufferedImage newBuff = op.filter(currentImg, null);
					
					ImageIO.write(newBuff, "png", outputfile);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(-2);
				}
				
		        final String dir = System.getProperty("user.dir");
		        System.out.println("current dir = " + dir);
		        
		        try {
					Process process = Runtime.getRuntime().exec("cmd /c start cmd.exe /c " + " start %windir%\\explorer.exe \""+dir+"\"");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        
			}
		});
		panel.add(wFile, "cell 0 15 3 1,growx,aligny top");
		
		JEditorPane dtrpnDragAndDrop = new JEditorPane();
		dtrpnDragAndDrop.setFont(new Font("Tahoma", Font.BOLD, 15));
		dtrpnDragAndDrop.setText("\r\n\r\n\r\n\r\n\r\n\r\nDrag and Drop \r\nImages Here");
		dtrpnDragAndDrop.setSize(100, 200);
		dtrpnDragAndDrop.setEditable(false);
		dtrpnDragAndDrop.setDragEnabled(true);
		
		dtrpnDragAndDrop.setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File file : droppedFiles) {
		            	System.out.println("file = " + file.toString());
		            	
		            	String mimetype = Files.probeContentType(file.toPath());

		            	if (mimetype != null && mimetype.split("/")[0].equals("image")) {
		            	    System.out.println("it is an image");
			        		currentImg = loadImg(file.toString(),false);
			        		drawImg();
			        		frmStopReverseImage.pack();
		            	}
		            	
		            	System.out.println("not image");
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		
		panel.add(dtrpnDragAndDrop,"cell 0 16 3 5,aligny bottom,grow" );

		frmStopReverseImage.pack();

	}
}
