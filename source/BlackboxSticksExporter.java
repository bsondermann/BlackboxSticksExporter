import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.nio.file.Files; 
import http.requests.*; 
import processing.sound.*; 
import java.io.PrintStream; 
import java.io.FileOutputStream; 
import java.awt.Color; 
import java.io.InputStreamReader; 
import interfascia.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BlackboxSticksExporter extends PApplet {






Selection s;
IFLookAndFeel look;
File inputPath,outputPath;
int inputFilesNum=0;
XML data;
RenderManager rm;
float version;
boolean newVersion=false;
public void setup(){
  
  final PImage icon = loadImage("assets/icon.png");
  surface.setIcon(icon);
  data = loadXML("assets/data.xml");
  if(data.getChildren("data")[0].getContent()!=""){
    inputPath=new File(data.getChildren("data")[0].getContent());
    inputFilesNum=getNumFiles(inputPath,"BFL");
  }
  if(data.getChildren("data")[1].getContent()!=""){
    outputPath=new File(data.getChildren("data")[1].getContent());
  }if(data.getChildren("data")[2].getContent()!=""){
    version = Float.parseFloat(data.getChildren("data")[2].getContent());
  }if(data.getChildren("data")[3].getContent()!=""){
    int last = Integer.parseInt(data.getChildren("data")[3].getContent());
    if(last!=hour()){
      newVersion=checkUpdates();
      data.getChildren("data")[3].setContent(hour()+"");
      saveXML(data,"assets/data.xml");
    }
  }else{
      newVersion=checkUpdates();
      data.getChildren("data")[3].setContent(hour()+"");
      saveXML(data,"assets/data.xml");
  }
  println(newVersion);
  look = new IFLookAndFeel(this,IFLookAndFeel.DEFAULT);
  look.textColor=color(0);
  if(!newVersion){
    s=new Selection(this,look);
  }
  thread("clearTemp");
  try{
    saveStrings(sketchPath()+"/assets/logs/log.txt",new String[]{""});
    saveStrings(sketchPath()+"/assets/logs/errlog.txt",new String[]{""});
    PrintStream err = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/errlog.txt",true),true);
    System.setErr(err);
    PrintStream out = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/log.txt",true),true);
    System.setOut(out);
  }catch(Exception e){
    e.printStackTrace();
  }
}
public void draw(){
  background(50);
  if(!newVersion){
    s.show();
    if(s.active()){
    if(inputPath!=null){
      String text=inputPath.getAbsolutePath()+" | "+inputFilesNum+" Logs found!";
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(text)+20)/2,58,textWidth(text)+20,22);
      fill(0);
      text(text,400,76);
    }if(outputPath!=null){      
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(outputPath.getAbsolutePath())+20)/2,118,textWidth(outputPath.getAbsolutePath())+20,22);
      fill(0);
      text(outputPath.getAbsolutePath(),400,137);
    }
  }
  }else{
    fill(255);
    textAlign(CENTER,CENTER);
    text("Yay! New version Available!",width/2,height/2);
  }
}
public void actionPerformed(GUIEvent e){
    s.actionPerformed(e);
}
public void InputSelected(File selection){
  if(selection!=null){
    inputPath=selection;
    inputFilesNum=getNumFiles(inputPath,"BFL");
    data.getChildren("data")[0].setContent(selection.getAbsolutePath());
    saveXML(data,"assets/data.xml");
    println(getNumFiles(inputPath,"BFL"));
  }
}
public void OutputSelected(File selection){
  if(selection!=null){
    data.getChildren("data")[1].setContent(selection.getAbsolutePath());
    saveXML(data,"assets/data.xml");
    outputPath=selection;
  }
}
public int getNumFiles(File path, String ending){
  int num=0;
  File[] files = path.listFiles();
  for(int i = 0; i<files.length; i++){
    if(files[i].getName().contains(ending)){
      num++;
    }
  }
  return num;
}
public void clearTemp(){
  deleteDir(new File(sketchPath()+"/temp/"));
  if(s!=null){
  s.tempClear=true;
  }
}
public void deleteDir(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
        for (File f : contents) {
            if (! Files.isSymbolicLink(f.toPath())) {
                deleteDir(f);
            }
        }
    }
    file.delete();
}
public void mouseDragged(){
  if(s!=null){
    s.mouseDown();
  }
}
public void mousePressed(){
if(s!=null){
    s.mousePress();
  }
}
public void mouseReleased(){
if(s!=null){
    s.mouseRel();
  }
}
public void exit(){
  if(s!=null){
    s.stop();
  }
  surface.setVisible(false);
  delay(2000);
  clearTemp();
  super.exit();
  
}
public boolean checkUpdates(){
  GetRequest get = new GetRequest("https://api.github.com/repos/bsondermann/BlackboxSticksExporter/releases/latest");
  get.send();
  
  if(Float.parseFloat(parseJSONObject(get.getContent()).getString("tag_name").trim().substring(1))>version){return true;}
  else{return false;}
}
public void shutdown(){
  try{
  Runtime r = Runtime.getRuntime();
  Process proc = r.exec("shutdown -s -t 0");
  exit();
}
  catch(Exception e){}
}

class ColorWheel{
  int w ,posx,posy;
  PImage ring;
  PImage rect;
  PVector posSelRing;
  PVector posSelRect;
  boolean ringSelected=false,rectSelected=false;
  Settings set;
  IFTextField f;
  ColorWheel(int w,int px,int py,Settings set,IFTextField f){
    
    posSelRing= new PVector(0,-1);
    posSelRect= new PVector(1,0);
    setColor(f.getValue());
    this.w = w;
    this.posx= px;
    this.posy = py;
    this.set = set;
    this.f =f;
    ring = createImage(w,w,ARGB);
    rect = createImage((int)(((w/2)*0.8f)/sqrt(2)*2),(int)(((w/2)*0.8f)/sqrt(2)*2),ARGB);
    ring.loadPixels();
    for(int x = 0; x<w; x++){
      for(int y = 0; y<w; y++){
        float d=dist(x,y,w/2,w/2);
        PVector vec = new PVector(x-w/2,y-w/2).normalize();
        float angle = vec.heading()/TWO_PI;
        if(d<w/2&&d>(w/2)*0.8f){
          ring.pixels[x+y*w]=Color.HSBtoRGB(angle,1,1);
        }else{
          
          ring.pixels[x+y*w]=color(0,0,0,0);
        }
      }
    }
    ring.updatePixels();
    calcRect();
  }
  
  public void show(){
    if(rectSelected){
        posSelRect.x= constrain(map(mouseX,posx+w/2-rect.width/2,posx+w/2+rect.width/2,0,1),0,1);
        posSelRect.y =constrain( map(mouseY,posy+w/2-rect.height/2,posy+w/2+rect.height/2,0,1),0,1);
        calcRect();
    }
    if(ringSelected){
      posSelRing = PVector.fromAngle(new PVector(mouseX-(posx+w/2),mouseY-(posy+w/2)).heading());
      calcRect();
    }
    imageMode(CORNER);
    image(ring,posx,posy);
    noFill();
    stroke(255);
    strokeWeight(w*0.015f);
    ellipseMode(CENTER);
    ellipse(posSelRing.x*((w/2)*0.9f)+w/2+posx,posSelRing.y*((w/2)*0.9f)+w/2+posy,(w/2)*0.1f,(w/2)*0.1f);
    imageMode(CENTER);
    image(rect,posx+w/2,posy+w/2);
    ellipse(map(posSelRect.x,0,1,posx+w/2-rect.width/2,posx+w/2+rect.width/2),map(posSelRect.y,0,1,posy+w/2-rect.height/2,posy+w/2+rect.height/2),(w/2)*0.1f,(w/2)*0.1f);
    fill(255,0,0);
    stroke(0);
    strokeWeight(1);
    rect(posx+w,posy-w/12,w/12,w/12);
    strokeWeight(3);
    stroke(100,0,0);
    line(posx+w+4,posy-w/12+4,posx+w+w/12-4,posy-4);
    line(posx+w+w/12-4,posy-w/12+4,posx+w+4,posy-4);
    
  }
  
  public void mousePress(){
    float d=dist(mouseX,mouseY,posx+w/2,posy+w/2);
    if(d<w/2&&d>(w/2)*0.8f&&!rectSelected){
      ringSelected=true;
    }
    
    if(mouseX>posx+w/2-rect.width/2&&mouseX<posx+w/2+rect.width/2&&mouseY>posy+w/2-rect.height/2&&mouseY<posy+w/2+rect.height/2&&!ringSelected){
      rectSelected=true;
    }
    if(mouseX>posx+w&&mouseX<posx+w+4+w/12&&mouseY>posy-w/12&&mouseY<posy-w/12+w/12){
      set.disableCW();
    }
  }
  public void mouseRel(){
    rectSelected=false;
    ringSelected=false;

  }
  public void calcRect(){
    rect.loadPixels();
    for(int x = 0; x <rect.width;x++){
      for(int y = 0; y <rect.height; y++){
        rect.pixels[x+y*rect.width]=Color.HSBtoRGB(posSelRing.heading()/TWO_PI,map(x,0,rect.width,0,1),map(y,0,rect.width,1,0));
      }
    }
    rect.updatePixels();
    f.setValue("#"+getHexColor());
  }
  public int getColor(){
    return Color.HSBtoRGB(posSelRing.heading()/TWO_PI,posSelRect.x,1-posSelRect.y);
  }
  public void setColor(String col){
    int r = Integer.parseInt(col.substring(1,3),16);
    int g = Integer.parseInt(col.substring(3,5),16);
    int b = Integer.parseInt(col.substring(5,7),16);
    float[] hsb = Color.RGBtoHSB(r,g,b,null);
    posSelRing = PVector.fromAngle(map(hsb[0],0,1,0,TWO_PI));
    posSelRect = new PVector(hsb[1],1-hsb[2]);
    println(hsb[0]+" "+hsb[1]+" "+hsb[2]);
  }
  public String getHexColor(){
    return hex(getColor()).substring(2);
  }
}
class Preview{
  int posy;
  float offset=20;
  int allFramesCount=0;
  int allDoneFramesCount=0;
  float scrollPos=0.0f;
  int scrollControlHeight=50;
  int windowHeight=0;
  RenderManager rm;
  Renderer[] renderers;
  Preview(int posy, RenderManager manager){
    this.posy=posy;
    this.rm = manager;
    renderers = rm.getRenderers();
    windowHeight=max((renderers.length+1)*50-(height-posy+20),0);
  }
  
  public void show(){
    offset=-map(scrollPos,0,1,0,windowHeight)+20;
    fill(25);
    stroke(0);
    rect(0,posy,width,height-20-posy);
    fill(38);
    rect(width-15,posy,20,height-15);
    fill(60);
    rect(width-15,map(scrollPos,0,1,posy,height-20-scrollControlHeight),15,scrollControlHeight);
    int posindex=0;
    /*noFill();
    strokeWeight(3);
    stroke(0);
    rect(0,posy,width,20);
    fill(255,0,0);
    rect(0,posy,map(constrain(allDoneFramesCount,0,allFramesCount),0,allFramesCount,0,width),20);
    strokeWeight(1);
    fill(150);
    stroke(0);
    textAlign(CENTER,TOP);
    text((int)map(constrain(allDoneFramesCount,0,allFramesCount),0,allFramesCount,0,100)+"%",(width)/2,posy);*/
    allDoneFramesCount=0;
    allFramesCount=0;
    for(int i = 0; i< renderers.length;i++){
      
      allFramesCount+=renderers[i].numFrames;
      allDoneFramesCount+= renderers[i].currentFrame;
      if(renderers[i].running){
        fill(150);
        textAlign(LEFT,TOP);
        text(renderers[i].file.getName().substring(0,renderers[i].file.getName().length()-4),0,posy+posindex*50+offset);
        textAlign(RIGHT,TOP);
        text(renderers[i].currentState,width-100,posy+posindex*50+offset);
        textAlign(CENTER,TOP);
        String sec1;
        if(renderers[i].getRuntime()%60<10){sec1="0"+renderers[i].getRuntime()%60;}else{sec1=renderers[i].getRuntime()%60+"";}
        String sec2;
        if(renderers[i].getETA()%60<10){sec2="0"+renderers[i].getETA()%60;}else{sec2=renderers[i].getETA()%60+"";}
        text(renderers[i].getRuntime()/60+":"+sec1+"    |    "+renderers[i].getETA()/60+":"+sec2,(width-100)/2,posy+posindex*50+offset);
        noFill();
        strokeWeight(3);
        stroke(0);
        rect(0,posy+posindex*50+offset+20,width-100,20);
        fill(255,0,0);
        rect(0,posy+posindex*50+offset+20,map(constrain(renderers[i].currentFrame,0,renderers[i].numFrames),0,renderers[i].numFrames,0,width-100),20);
        strokeWeight(1);
        fill(150);
        stroke(0);
        textAlign(CENTER,TOP);
        text((int)map(constrain(renderers[i].currentFrame,0,renderers[i].numFrames),0,renderers[i].numFrames,0,100)+"%",(width-100)/2,posy+posindex*50+offset+24);
        fill(50);
        strokeWeight(3);
        rect(width-97,posy+posindex*50+offset,80,40);
        strokeWeight(1);
        if(renderers[i].prevImage!=null){
          image(renderers[i].prevImage,width-97,posy+posindex*50+offset+2,80,40);
        }
        posindex++;
      }
      
    }
    int posindex2=0;
    for(int i = 0; i< renderers.length;i++){
      if(!renderers[i].running){
        fill(150);
        textAlign(LEFT,TOP);
        text(renderers[i].file.getName().substring(0,renderers[i].file.getName().length()-4),0,posy+(posindex+posindex2)*50+offset);
        textAlign(RIGHT,TOP);
        text(renderers[i].currentState,width-100,posy+(posindex+posindex2)*50+offset);
        posindex2++;
      }
    }
    fill(50);
    rect(0,0,width,posy);
    
  }
  public void mouseDown(){
  if(mouseX>width-15&&mouseY>map(scrollPos,0,1,posy,height-20-scrollControlHeight)&&mouseY<map(scrollPos,0,1,posy,height-20-scrollControlHeight)+scrollControlHeight);{
    scrollPos=constrain(map(mouseY-scrollControlHeight/2,posy,height-20-scrollControlHeight,0,1),0,1);
  }
  }

}
class RenderManager extends Thread{
  boolean rendering=false;
  File files[];
  Renderer renderers[];
  String[]renderSettings;
  int simultRenderNum;
  int activeRenderers;
  int indexRenderer=0;
  Selection s;
  RenderManager(File[] f,String[]set,Selection s){
    this.s=s;
    simultRenderNum=Integer.parseInt(set[9]);
    renderSettings=set;
    filterFiles(f);
    initRenderers();
    start();
  }
  public @Override void run(){
    rendering=true;
    while(indexRenderer<renderers.length){
      if(activeRenderers<simultRenderNum){
        renderers[indexRenderer].startRender();
        activeRenderers++;
        indexRenderer++;
      }
      for(int i = 0; i<indexRenderer;i++){
        if(renderers[i].currentState.equals("Done")){
          renderers[i].currentState="Done | Idle";
          activeRenderers--;
        }
      }
    }
    boolean done = false;
    while(!done){
      done = true;
      for(int i = 0; i<renderers.length;i++){
        if(renderers[i].running){
          done = false;
        }
        if(renderers[i].currentState.equals("Done")){
          renderers[i].currentState="Done | Idle";
        }
      }
    }
    s.doneRendering();
  }
  public void initRenderers(){
    renderers = new Renderer[files.length];
    for(int i = 0; i<renderers.length; i++){
      renderers[i] = new Renderer(files[i],renderSettings,i);
    }
  }
  public void filterFiles(File[]f){
    int filenum =0;
    for(int i = 0; i< f.length;i++){
      if(f[i].getName().contains(".BFL")){
        filenum++;
      }
    }
    files= new File[filenum];
    int index=0;
    for(int i = 0; i< f.length;i++){
      if(f[i].getName().contains(".BFL")){
        files[index]=f[i];
        index++;
      }
    }
  }
  public Renderer[] getRenderers(){
    return renderers;
  }
  public void stopRender(){
    if(rendering){
      for(int i = 0; i< renderers.length;i++){
        renderers[i].stopRender();
      }
      rendering=false;
    }
  }
}

class Renderer extends Thread{
  File file;
  boolean running = false,stop=false;
  int numFrames, currentFrame=0;
  String[]settings;
  String currentState="";
  int number;
  int fps,tailLength,vidWidth,borderThickness,bgOpacity,sticksMode,sticksModeVertPos;
  int bgColor,sticksColor;
  PImage prevImage;
  int starttime=0;
  Renderer(File f,String[]s,int number){
    currentState="Idle";
    this.number= number;

    settings = s;
    file = f;
    parseSettings();
  }
  public void convertLog(){
    
    if(stop){return;}
    currentState="converting";
    createImage(1, 1, RGB).save(sketchPath()+"/temp/"+number+"/csv/temp.png");
    new File(sketchPath()+"/temp/"+number+"/csv/temp.png").delete();
    ProcessBuilder pb = new ProcessBuilder(sketchPath()+"/assets/blackbox_decode.exe","--merge-gps",""+file.getAbsolutePath());
    try {
    Process process = pb.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      println(line);

      line= reader.readLine();
    }
    File del = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.event");
    del.delete();
    del = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.gps.csv");
    del.delete();
    file =new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".01.csv");
    file.renameTo(new File(sketchPath()+"/temp/"+number+"/csv/"+file.getName()));
    file = new File(sketchPath()+"/temp/"+number+"/csv/"+file.getName());
    Table table = loadTable(file.getAbsolutePath(),"header");
    float lengthus=(PApplet.parseInt(table.getRow(table.getRowCount()-1).getString(1).trim())-PApplet.parseInt(table.getRow(1).getString(1).trim()))*1.002305f;
    numFrames=(int)((lengthus/1000000f)*fps);
    
    }catch(Exception e) {
    e.printStackTrace();
    }
  }
  public void renderLog(){
    
    if(stop){return;}
    currentState="Rendering";
    int w = vidWidth;
    Table table = loadTable(file.getAbsolutePath(),"header");
    PGraphics alphaG=createGraphics(vidWidth,vidWidth/2);
    int startindex=1;
    float lengthus=(PApplet.parseFloat(table.getRow(table.getRowCount()-1).getString(1).trim())-PApplet.parseFloat(table.getRow(startindex).getString(1).trim()))*1.002305f;
    int lengthlist = table.getRowCount()-startindex;
    numFrames=(int)((lengthus/1000000.0f)*PApplet.parseFloat(fps));
    float space=PApplet.parseFloat(lengthlist)/((lengthus/1000000.0f)*PApplet.parseFloat(fps));
  int where=0;
  for (float i = startindex; i<table.getRowCount(); i+=space) {
    if(stop){return;}
    TableRow row = table.getRow(PApplet.parseInt(i));
    currentFrame++;
    alphaG.beginDraw();
    //alphaG.translate(xoff, yoff);
    alphaG.clear();
    alphaG.fill(255, 255, 255, 255);
    alphaG.stroke(0, 0, 0, 255);
    alphaG.strokeWeight(w/400);
    //vert
    alphaG.rect(w/2-w/30-w/6-w/400, ((w/3)/7.3f)/2, w/200, w/3 );
    alphaG.rect(w/2+w/30+w/6-w/400, ((w/3)/7.3f)/2, w/200, w/3 );
    //hori
    alphaG.rect(w/2-w/30-w/3, w/6-w/400+((w/3)/7.3f)/2, w/3, w/200);
    alphaG.rect(w/2+w/30, w/6-w/400+((w/3)/7.3f)/2, w/3, w/200);

    //text
    alphaG.textSize(w/25);
    if(sticksMode == 2){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3f)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(PApplet.parseInt(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-PApplet.parseInt(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(PApplet.parseInt(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75);
    }else if(sticksMode ==3){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(PApplet.parseInt(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3f)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(PApplet.parseInt(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-PApplet.parseInt(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75);
    
    }if(sticksMode == 1){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(PApplet.parseInt(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3f)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-PApplet.parseInt(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(PApplet.parseInt(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75);
    }if(sticksMode == 4){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3f)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(PApplet.parseInt(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(PApplet.parseInt(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-PApplet.parseInt(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75);
    }
    
    

    alphaG.noStroke();
    float taillength=(fps/30)*tailLength;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    for (float j = 0; j< space*taillength; j++) {
      TableRow trailrow = table.getRow(constrain(PApplet.parseInt(i-j), 0, table.getRowCount()-1));
      alphaG.fill(175, 175, 175, map(j, 0, space*taillength, 100, 0));
      float r = map(j, 0, space*taillength, ((w/3)/7.3f)/1.5f, ((w/3)/7.3f)/10);
      float x=0, y=0, x1=0, y1=0;
      if (sticksMode==2) {
        //yaw
        x =map(-PApplet.parseInt(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(PApplet.parseInt(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
        //roll
        x1= map(PApplet.parseInt(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-PApplet.parseInt(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
      }
      if (sticksMode==3) {
        //yaw
        x =map(PApplet.parseInt(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-PApplet.parseInt(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
        //roll
        x1= map(-PApplet.parseInt(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(PApplet.parseInt(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
      }
      if (sticksMode==1) {
        //yaw
        x =map(-PApplet.parseInt(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-PApplet.parseInt(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
        //roll
        x1= map(PApplet.parseInt(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(PApplet.parseInt(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
      }
      if (sticksMode==4) {
        //yaw
        x =map(PApplet.parseInt(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(PApplet.parseInt(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
        //roll
        x1= map(-PApplet.parseInt(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-PApplet.parseInt(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2;
      }
      if (x!=prevx&&y!=prevy) {
        alphaG.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1&&y1!=prevy1) {
        alphaG.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
    }
    alphaG.fill(255,255,255,255);
    alphaG.textAlign(CENTER,CENTER);
    if(sticksMode==2||sticksMode==4){
    alphaG.text("Mode"+sticksMode,w/2-w/30-w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3f)/2,(((w/3)/7.3f)/2)+w/3));
    }else{
    alphaG.text("Mode"+sticksMode,w/2+w/30+w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3f)/2,(((w/3)/7.3f)/2)+w/3));
    }
    alphaG.fill(sticksColor, 255);
    if (sticksMode==2) {
      alphaG.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
      alphaG.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
    }else if(sticksMode==3){
      alphaG.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
      alphaG.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
    
      }
    else if(sticksMode==1){
       alphaG.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
      alphaG.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
    }if (sticksMode==4) {
      alphaG.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
      alphaG.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f, (w/3)/7.3f);
    }
    alphaG.endDraw();
    PImage border = createImage(alphaG.width, alphaG.height, ARGB);
    if (borderThickness>0) {
      border.loadPixels();
      for (int j = 0; j<border.pixels.length; j++) {
        border.pixels[j]=0;
        float m=9999;
        for (int x = -borderThickness; x<borderThickness; x++) {
          if(m!=0){
            for (int y = -borderThickness; y<borderThickness; y++) {
              if(m!=0){
                if(((alphaG.pixels[constrain(j+x+(y*alphaG.width),0,border.pixels.length-1)]>>24)&0xFF)!=0){
                  m=min(dist(0,0,x,y),m);
                }
              }
            }
          }
        }
        border.pixels[j] = ((int)(map(constrain(m,0,PApplet.parseFloat(borderThickness)*sqrt(2)),PApplet.parseFloat(borderThickness)*sqrt(2),0,0,255))<<24);
      }
      border.updatePixels();
    }

    PGraphics out = createGraphics(alphaG.width, alphaG.height, JAVA2D);
    out.beginDraw();
    out.clear();
    if (bgOpacity>0) {
      out.background(bgColor, bgOpacity);
    }
    if (borderThickness>0) {

      out.image(border, 0,0);
    }
    out.image(alphaG, 0, 0);

    out.endDraw();
    out.save("temp/"+number+"/Images/line_"+("0000000000"+where).substring((where+"").length())+".png"); 

    prevImage = out;
    where++;
  }
  
  }
  public void compileLog(){
    if(stop){return;}
    currentState="Compiling";
    ProcessBuilder processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe", "-r", fps+"", "-i", sketchPath()+"/temp/"+number+"/Images/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", outputPath.getAbsolutePath()+"/"+file.getName().substring(0,file.getName().length()-4)+".mov");
  try {
    Process process = processBuilder.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      if (line.contains("frame")) {
        currentFrame=Integer.parseInt(line.substring(7, 13).replaceAll("[\\D]", ""));
      }
      println(line);
      line= reader.readLine();
    }
  }
  catch(Exception e) {
  }
  }
  public void startRender(){
    running=true;
    start();
  }
  public void parseSettings(){
    fps=Integer.parseInt(settings[0]);
    tailLength=Integer.parseInt(settings[1]);
    vidWidth=Integer.parseInt(settings[2]);
    borderThickness=Integer.parseInt(settings[3]);
    bgOpacity=Integer.parseInt(settings[5]);
    sticksMode=Integer.parseInt(settings[6]);
    sticksModeVertPos=Integer.parseInt(settings[7]);
    bgColor = color(Integer.parseInt(settings[4].substring(1, 3), 16), Integer.parseInt(settings[4].substring(3, 5), 16), Integer.parseInt(settings[4].substring(5, 7), 16));
    sticksColor = color(Integer.parseInt(settings[8].substring(1, 3), 16), Integer.parseInt(settings[8].substring(3, 5), 16), Integer.parseInt(settings[8].substring(5, 7), 16));
    
  }
  public @Override void run(){
    starttime=millis();
    if(stop){return;}
    convertLog();
    
    if(stop){return;}
        starttime=millis();
    renderLog();
    if(stop){return;}
        starttime=millis();
    compileLog();
    currentState="Done";
    
    running=false;
  }
  public int getETA(){
    float percent = PApplet.parseFloat(currentFrame)/numFrames;
    int dt = millis()-starttime;
    return (int)max(((dt/percent-millis()+starttime)/1000),0);
  }
  public int getRuntime(){
    return (int)((millis()-starttime)/1000);
  }
  public void stopRender(){
    stop=true;
  }
}

class Selection{
  Preview p;
  boolean active = false;
  boolean rendering=false;
  boolean tempClear=false;
  GUIController c1;
  IFButton settings,btnInputFolder,btnOutputFolder,btnStart,addRenderer;
  IFCheckBox checkSound, checkShutdown;
  PApplet applet;
  Settings set;
  IFLookAndFeel look;
  SoundFile sound;
  String console="";
  boolean readyToRender =false;
  
  Selection(PApplet applet, IFLookAndFeel look){
    active = true;
    this.applet = applet;
    this.look = look;
    createSelectionGui();
   sound = new SoundFile(applet,"assets/done.wav");
  }
  public void show(){
    
    if(set!=null){set.show();}
    if(rendering){
      p.show();
    }
    
    textAlign(CENTER,BOTTOM);
    fill(100);
    stroke(0);
    rect(0,height-20,width,20);
    fill(0);
    text(console,width/2,height-2);
    
  }
  
  public void actionPerformed(GUIEvent e){
    if(this.active){
      if(e.getSource()==settings){
        if(rm!=null&&rm.rendering){
          console="You can't open settings while rendering!";
        }else{
        println("Open Settings");
        removeSelectionGui();
        set = new Settings(applet,this.look,this);
        }  
      }
      if(e.getSource() == btnInputFolder){
        selectFolder("Select Input Folder","InputSelected");
      }
      if(e.getSource() == btnOutputFolder){
        selectFolder("Select Output Folder","OutputSelected");
      }if(e.getSource()==addRenderer){
        if(rm!=null){
          println("kot");
          rm.simultRenderNum++;
          XML xml = loadXML("assets/settings.xml");
          XML[] children = xml.getChildren("setting");
          children[9].setContent(rm.simultRenderNum+"");
          saveXML(xml,"assets/settings.xml");
        }
      }
      if(e.getSource()==btnStart){
        readyToRender = true;
        if(inputPath==null){
          console="Error: Select Input Folder!";
          readyToRender = false;
        }
        if(outputPath==null){
          console="Error: Select Output Folder!";
          readyToRender = false;
        }
        if(outputPath==null&&inputPath==null){
          console="Error: Select Input and Output Folder!";
          readyToRender = false;
        }
        if(outputPath!=null&&inputPath!=null){
          if(outputPath.getAbsolutePath().equals(inputPath.getAbsolutePath())){
            console="Error: Input Folder and Output Folder are the same!";
            readyToRender = false;
          }
        }
        if(inputPath!=null){
          if(getNumFiles(inputPath,"BFL")<=0){
            console="Error: No logs in selected Folder";
            readyToRender=false;
          }
        }
        if(!tempClear){
          console="Error: wait until temp is cleared!";
          readyToRender=false;
        }
        if(readyToRender==true&&rm==null){
          
          console="Starting...";
          rm=new RenderManager(inputPath.listFiles(),getCurrentSettings(),this);
          rendering = true;
          p = new Preview(200,rm);
        }
      }
    }
    if(set!=null){
    set.actionPerformed(e);
    }
  }
  
  public boolean active(){
    return this.active;
  }
  public void createSelectionGui(){
    c1 = new GUIController(applet);
    c1.setLookAndFeel(look);
    settings = new IFButton("Change Settings",50,30,100,20);
    settings.addActionListener(applet);
    btnInputFolder = new IFButton("Select Input Folder",325,30,150,20);
    btnInputFolder.addActionListener(applet);
    btnOutputFolder = new IFButton("Select Output Folder",325,90,150,20);
    btnOutputFolder.addActionListener(applet);
    btnStart = new IFButton("Start",325,150,150,20);
    btnStart.addActionListener(applet);
    checkSound = new IFCheckBox("Finish Sound",700,30);
    c1.add(checkSound);
    checkShutdown = new IFCheckBox("Shutdown",700,50);
    addRenderer = new IFButton("Add Renderer",50,90,100,20);
    addRenderer.addActionListener(applet);
    c1.add(addRenderer);
    c1.add(checkShutdown);
    c1.add(btnInputFolder);
    c1.add(btnStart);
    c1.add(btnOutputFolder);
    c1.add(settings);
    active = true;
  }
  public void removeSelectionGui(){
    c1.remove(settings);
    c1.remove(btnInputFolder);
    c1.remove(btnOutputFolder);
    settings.addActionListener(null);
    btnInputFolder.addActionListener(null);
    btnOutputFolder.addActionListener(null);
    c1.setVisible(false);
    c1=null;
    settings = null;
    active=false;
  }
  
  public String[]getCurrentSettings(){
    XML xml = loadXML("assets/settings.xml");
    XML[] children = xml.getChildren("setting");
    String[]settings = new String[children.length];
    for(int i = 0; i< children.length; i++){
      settings[i] = children[i].getContent();
    }
    return settings;
  }
  public void mouseDown(){
    if(rendering){
      p.mouseDown();
    }
  }
  public void stop(){
    if(rm!=null){
      rm.stopRender();
    }
  }
  public void doneRendering(){
    if(checkShutdown.isSelected()){
      shutdown();
    }
    if(checkSound.isSelected()){
      
      sound.play();
    }
    clearTemp();
  }
  public void mousePress(){
    if(set!=null){
      set.mousePress();
      
    }
  }
  public void mouseRel(){
    if(set!=null){
      set.mouseRel();
      
    }
  }
}

class Settings{
  boolean active = false;
  GUIController c;
  PApplet applet;
  XML xml;
  String explanations[]={"Frames per Second of the final Video.\nDefault: 60, Recommended: 30, 60, 120","Traillength sets the length of the Trail behind the Stick","Width determens the width of the final Video.\nThe hight is automatically calculated with a ratio of 2:1","BorderThickness sets the width of a 'Shadow' surrounding the contours\n(increases render time a lot). Values: 0 to 100","BackgroundColor sets the color of the entire background.\nValues: #000000 to #FFFFFF (Use a online colorpicker and copy the hex color)","BackgroundOpacity sets the transparency of the Background.\nValues: 0 (100% transparent) to 100 (0% transparent)","SticksMode sets the sticks mode of the final Video.\nValues: 1 to 4","SticksModeVertPos sets the vertical position of the sticks mode information.\nThe horizontal position is automatically determined and on the throttle stick.\nValues: 0 (top) to 100 (bottom)","StickColor sets the color of the sticks.\nValues: #000000 to #FFFFFF (Use a online colorpicker and copy the hex color)","SimultaneousRenders sets the maximum number of simultaneous renderers."};
  IFLabel labels[];
  String labelstext[];
  IFLabel explain;
  IFTextField fields[];
  IFLookAndFeel look;
  IFButton back;
  IFButton save;
  IFButton defaults;
  Selection selection;
  ColorWheel cw;
  Settings(PApplet applet, IFLookAndFeel look,Selection selection){
    this.applet = applet;
    this.look = look;
    this.selection = selection;
    
    c = new GUIController(applet);
    c.setLookAndFeel(look);
    initXML();
    createSettingsGui();
    
  }
  
  public boolean active(){return this.active;}
  public void show(){
    explain.setLabel("");

    if(labels.length!=0){
      fill(100);
      rect(40,70,720,30*labels.length+10);
      for(int i =0; i< labels.length-1; i++){
        line(50,105+30*i,750,105+30*i);
      }
      for(int i = 0; i<fields.length;i++){
        fill(0);
        textAlign(LEFT,TOP);
        text(labelstext[i],50,85+30*i);
        
      }
      for(int i = 0; i<fields.length;i++){

        if(labels[i].isMouseOver(mouseX,mouseY)){
          fill(255);
          rectMode(CORNER);
          stroke(0);
          rect(5+mouseX,mouseY-5,textWidth(explanations[i])+10,textHeight(explanations[i])+20);
          explain.setLabel(explanations[i]);
          explain.setPosition(10+mouseX,mouseY);
        }
      }
      
    }
    if(cw!=null){
      stroke(0);
      fill(100);
      rect(cw.posx-20,cw.posy-20,cw.w+40,cw.w+40);
      cw.show();
      strokeWeight(1);
      
    }
  }
  public void actionPerformed(GUIEvent e){
    if(this.active){
    if(e.getSource()==back){
      removeSettingsGui();
    }else if(e.getSource()==save){
      checkColor();
      writeXML();
   }else if(e.getSource()==defaults){
     XML[] newchildren=loadXML("assets/data.xml").getChildren("setting");
     XML[] children = xml.getChildren("setting");
     for(int i = 0; i< children.length; i++){
       children[i].setContent(newchildren[i].getContent());
     }
     saveXML(xml,"assets/settings.xml");
     initXML();
   }else{
      IFTextField temp = (IFTextField)e.getSource();
      String type = temp.getLabel();
      String content = temp.getValue();
      if(type.contains("num")){
        String[]args=type.split(",");
        content = content.replaceAll("[^0-9]","");
        int numcontent = Integer.parseInt(content);
        if(args.length==3){
          numcontent = constrain(Integer.parseInt(content),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }else if(args.length==2){
          numcontent = max(Integer.parseInt(content),Integer.parseInt(args[1]));
        }
        temp.setValue(""+abs(numcontent));
      }else if(type.contains("col")){
        if(!content.contains("#")){
          content = "#"+content;
        }
        content = content.toUpperCase();
        content = content.replaceAll("[^A-F0-9#]","");
        content = content.substring(0,Math.min(content.length(),7));
        temp.setValue(content);
      }
   }}
    
  }
  public void createSettingsGui(){
    back = new IFButton("Back",50,30,100,20);
    back.addActionListener(applet);
    save = new IFButton("Save",width-150,30,100,20);
    save.addActionListener(applet);
    explain = new IFLabel("",-100,-100);
    defaults = new IFButton("Load Defaults",width/2-50,30,100,20);
    defaults.addActionListener(applet);
    c.add(defaults);
    c.add(back);
    c.add(save);
        c.add(explain);
    active = true;
  }
  public void removeSettingsGui(){
    for(int i = 0; i< labels.length;i++){
      c.remove(labels[i]);
      c.remove(fields[i]);
    }
    c.remove(save);
    save.addActionListener(null);
    save=null;
    fields=new IFTextField[0];
    labels = new IFLabel[0];
    c.remove(back);
    back.addActionListener(null);
    back=null;
    c.setVisible(false);
    c=null;
    active = false;
    selection.createSelectionGui();
    cw=null;
  }
  public void initXML(){
    
    xml=loadXML("assets/settings.xml");
    XML[] children = xml.getChildren("setting");
    labelstext = new String[children.length]; 
    labels = new IFLabel[children.length];
    fields = new IFTextField[children.length];
    for(int i = 0; i< children.length;i++){
      labels[i] = new IFLabel("",50,85+30*i);
      labels[i].setWidth(150);
      labels[i].setHeight(20);
      labelstext[i] = children[i].getString("id").trim();
      c.add(labels[i]);
      fields[i] = new IFTextField(children[i].getString("type").trim(),600,80+30*i,150);
      fields[i].setValue( children[i].getContent());
      fields[i].addActionListener(applet);
      c.add(fields[i]);
      //labels[i].setLabel(children[i].getString("id").trim());
    }
  }
  public void writeXML(){
    XML[] children = xml.getChildren("setting");
    for(int i = 0; i<children.length;i++){
      children[i].setContent(fields[i].getValue());
    }
    saveXML(xml,"assets/settings.xml");
    s.console = "Settings saved!";
  }
  public void checkColor(){
    for(int i = 0; i< fields.length;i++){
      if(fields[i].getValue().contains("#")){
        String cont = fields[i].getValue();
        int l = cont.length();
        
        for(int j = 0; j<7-l;j++){
          cont=cont+"0";
        }
        fields[i].setValue(cont);
      }
    }
  }
  public int textHeight(String text){
    return (text.split("\n", -1).length-1)*18;
  }
  public void mousePress(){
    if(mouseButton == 39){
      for(int i = 0; i< fields.length; i++){
        if(fields[i].getLabel().contains("col")&&fields[i].isMouseOver(mouseX,mouseY)){
          cw = new ColorWheel(200,width/2-100,mouseY-100,this,fields[i]);
        }
      }
    }
    if(cw!=null){
      cw.mousePress();
    }
  }
  public void mouseRel(){
    if(cw!=null){
      cw.mouseRel();
    }
  }
  public void disableCW(){
    cw=null;
  }
}
  public void settings() {  size(800,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BlackboxSticksExporter" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
