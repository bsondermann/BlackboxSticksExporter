import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.InputStreamReader; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BlackboxSticksExporter extends PApplet {



int w = 500;
int fps = 24;
float tl=50;
XML xml;
int val;
int num;
boolean gotit=false;
boolean[]rendering;
boolean[]compiling;
boolean[]done;
int[] currrender;
int[] numframes;
String[]names;
boolean exited=false;
public void setup(){
  
  num= new File(sketchPath()+"/LOGS").listFiles().length-1;
  surface.setResizable(true);
  surface.setSize(width,(60*(num+1))+20);
  xml = loadXML("settings.xml");
  XML[] children = xml.getChildren("setting");
  for(int i = 0; i< children.length;i++){
    if(children[i].getString("id").equals("fps")){fps = Integer.parseInt(children[i].getContent());
    }else if(children[i].getString("id").equals("width")){w = Integer.parseInt(children[i].getContent());}
    else if(children[i].getString("id").equals("taillength")){tl = Float.parseFloat(children[i].getContent());
  }
  }
  background(0);
  rendering= new boolean[num+1];
  compiling= new boolean[num+1];
  done= new boolean[num+1];
  currrender = new int[num+1];
  numframes = new int[num+1];
  names = new String[num+1];
    val=0;
  int done=0;
  thread("calc");
  while(done<num){
    if(gotit){
      gotit=false;
      val++;
      thread("calc");
      done++;
    }
  }
}
public void draw(){
  background(0);
  textAlign(LEFT,TOP);
  textSize(20);
  fill(255);
  
  stroke(0);
  strokeWeight(2);
  for(int i = 0; i< rendering.length;i++){
    textSize(20);
    if(rendering[i]||compiling[i]){
    if(rendering[i]){
      text("rendering",0,40*i);
    }
    if(compiling[i]){
      text("compiling",0,40*i);
    }
    rectMode(CORNER);
    rect(0,20+40*i,150,20);
    fill(255,0,0);
    rect(0,20+40*i,map((currrender[i]+1.0f)/PApplet.parseFloat(numframes[i]),0,1,0,150),20);
    textSize(15);
    textAlign(CENTER,CENTER);
    stroke(255);
    fill(50);
    rect(500,40*i,88,40);
    PImage img = loadImage(sketchPath()+"/tempImages/"+i+"/line_"+("0000000000"+(constrain(currrender[i]-5,0,999999999))).substring(((constrain(currrender[i]-5,0,999999999))+"").length())+".png");
    if(img!=null){
    image(img,500,40*i,88,40);
    }
    stroke(0);
    fill(0);
    
    rectMode(CENTER);
    text(constrain((int)(100*((currrender[i]+1.0f)/PApplet.parseFloat(numframes[i]))),0,100)+"%",75,28+40*i);
    textAlign(LEFT,TOP);
    fill(255);
  }
  textSize(20);
  if(done[i]){
    text("DONE!",0,0+40*i);
  }
  text(names[i],150,40*i);
  }
  text("Settings: FPS: "+fps+", WIDTH: "+w+", TAILLENGTH: "+tl,0,height-20);
}
public void calc(){
  
  ellipseMode(CENTER);
  rectMode(CENTER);
  int n=val;
  gotit=true;
  File[] logs= new File(sketchPath()+"/LOGS").listFiles();

  names[n]=logs[n].getName();
  Table table;
  PGraphics alphaG;
float space;
  ProcessBuilder processBuilder;
  alphaG = createGraphics(w,(int)(w/2.2f), JAVA2D);
  table = loadTable(logs[n]+"","csv");
  int startindex=0;
  for(int i = 0; i< table.getRowCount();i++){
    TableRow row = table.getRow(i);
    int num=-1;
    try{
      num = Integer.parseInt(row.getString(0));
    }catch(Exception e){}
    if(num==-1){startindex=i+1;}
  }
  
  int lengthus=table.getRow(table.getRowCount()-1).getInt(1)-table.getRow(startindex).getInt(1);
  numframes[n] = (int)((PApplet.parseFloat(lengthus)/1000000f)*fps);
  rendering[n]=true;
  int lengthlist = table.getRowCount()-startindex;
  space=PApplet.parseFloat(lengthlist)/numframes[n];
  int where=0;
  for(int i = startindex; i<table.getRowCount();i+=space){
        if(exited){
       return;   
     }
    TableRow row = table.getRow(i);

      currrender[n]++;
    alphaG.beginDraw();
    alphaG.clear();
    alphaG.fill(255,255,255,255);
    alphaG.stroke(0,0,0,255);
    alphaG.strokeWeight(w/400);
    //vert
    alphaG.rect(w/2-w/30-w/6-w/400,    ((w/3)/7.3f)/2,              w/200,    w/3 );
    alphaG.rect(w/2+w/30+w/6-w/400,    ((w/3)/7.3f)/2,              w/200,    w/3 );
    //hori
    alphaG.rect(w/2-w/30-w/3,              w/6-w/400+((w/3)/7.3f)/2,        w/3,    w/200);
    alphaG.rect(w/2+w/30,                      w/6-w/400+((w/3)/7.3f)/2,        w/3,    w/200);
    
    //text
    alphaG.textSize(w/25);
    alphaG.textAlign(LEFT,CENTER);
    alphaG.text((int)map(constrain(-(int)map(row.getInt(16),1000,2000,-500,500),-500,500),-500,500,2000,1000)+"",0,w/6-w/200+((w/3)/7.3f)/2);
    alphaG.textAlign(RIGHT,CENTER);
    alphaG.text(row.getInt(14)+"",w,w/6-w/200+((w/3)/7.3f)/2);
    alphaG.textAlign(CENTER,BOTTOM);
    alphaG.text(-row.getInt(15)+"",w/2-w/30-w/6-w/400,alphaG.height);
    alphaG.textAlign(CENTER,BOTTOM);
    alphaG.text(row.getInt(13)+"",w/2+w/30+w/6-w/400,alphaG.height);
    

    alphaG.noStroke();
    float taillength=(fps/30)*tl;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    
    for(int j = 0; j< space*taillength;j++){
      TableRow trailrow = table.getRow(constrain(i-j,0,table.getRowCount()-1));
          alphaG.fill(175,175,175,map(j,0,space*taillength,100,0));
          float r = map(j,0,space*taillength,((w/3)/7.3f)/1.5f,((w/3)/7.3f)/10);
          float x =map(-trailrow.getInt(15),-500,500,w/2-w/30-w/3,w/2-w/30-w/3+w/3);
          float y = map(-(int)map(trailrow.getInt(16),1000,2000,-500,500),-500,500,0,w/3)+((w/3)/7.3f)/2;
          float x1= map(trailrow.getInt(13),-500,500,w/2+w/30,w/2+w/30+w/3);
          float y1 = map(-trailrow.getInt(14),-500,500,0,w/3)+((w/3)/7.3f)/2;
          if(x!=prevx&&y!=prevy){
            alphaG.ellipse(x,y,r,r);
            prevx=x;
            prevy=y;
          }
          if(x1!=prevx1&&y1!=prevy1){
            alphaG.ellipse(x1,y1,r,r);
          prevx1=x1;
          prevy1 =y1;
        }
    }
        alphaG.fill(255,102,102,255);
    alphaG.ellipse(map(-row.getInt(15),-500,500,w/2-w/30-w/3,w/2-w/30-w/3+w/3),map(-(int)map(row.getInt(16),1000,2000,-500,500),-500,500,0,w/3)+((w/3)/7.3f)/2,(w/3)/7.3f,(w/3)/7.3f);
    alphaG.ellipse(map(row.getInt(13),-500,500,w/2+w/30,w/2+w/30+w/3),map(-row.getInt(14),-500,500,0,w/3)+((w/3)/7.3f)/2,(w/3)/7.3f,(w/3)/7.3f);
    alphaG.endDraw();
    
    
    alphaG.save("tempImages/"+n+"/line_"+("0000000000"+where).substring((where+"").length())+".png"); 
 
  
   where++;
  }
 
    
  

    

  
  rendering[n]=false;
  compiling[n]=true;
  processBuilder = new ProcessBuilder(sketchPath()+"/ffmpeg.exe","-r",fps+"","-i",sketchPath()+"/tempImages/"+n+"/line_%010d.png","-vcodec","prores_ks","-pix_fmt","yuva444p10le","-profile:v","4444","-q:v","30","-r",fps+"","-y",sketchPath()+"/OUTPUT/"+logs[n].getName().substring(0,logs[n].getName().length()-4)+".mov");
  try{
  Process process = processBuilder.start();
  InputStream is = process.getErrorStream();
  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
  String line = reader.readLine();
  while(line !=null){
    if(line.contains("frame")){
      currrender[n]=Integer.parseInt(line.substring(7,13).replaceAll("[\\D]", ""));
    }
  println(line);
  line= reader.readLine();
}
  }catch(Exception e){}
  File[] files= new File(sketchPath()+"/tempImages/"+n+"/").listFiles();
  
  for(File f:files){
    f.delete();
  }
  compiling[n]=false;
  done[n]=true;
}

public void exit() {
  exited=true;
  delay(500);
  for(int i = 0; i<val+1; i++){
  File[] files= new File(sketchPath()+"/tempImages/"+i+"/").listFiles();
  
  for(File f:files){
    f.delete();
  }
  }
  super.exit();
}
  public void settings() {  size(600,80); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BlackboxSticksExporter" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
