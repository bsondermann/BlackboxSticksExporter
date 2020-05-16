import java.io.InputStreamReader;
import http.requests.*;

int w = 500;
int realw=500;
int xoff=0;
int yoff=0;
int fps = 24;
float ver=1.9;
boolean newerver;
int numf=1;
float tl=50;
XML xml;
int val;
int num;
int mode;
int borderThickness=0;
boolean gotit=false;
boolean converted=false;
boolean loaded=false;
boolean assigned=false;
boolean[]rendering;
boolean[]compiling;
boolean[]done;
int[] currrender;
int[] numframes;
String[]names;
boolean exited=false;
color backgroundColor;
int backgroundAlpha;
void setup() {
  size(600, 80);
  newerver = checkVersion();
  if(!newerver){
  textSize(20);
  fill(255);

  stroke(0);
  strokeWeight(2);

  background(0);
  clearTemp();
  init();

  thread("convertLogs");
  thread("loadLogs");
  thread("assignThreads");
  }else{
    text("newer version available",0,0);
  }
}
void draw() {
  
  background(0);
  textAlign(LEFT, TOP);
  textSize(20);
  fill(255);

  stroke(0);
  strokeWeight(2);
  if(!newerver){
    if(numf!=0){
  if (!converted&&!loaded&&!assigned) {
    text("converting Logs", 0, 0);
  }
  if (converted&&loaded&&!assigned) {
    text("loading Logs", 0, 0);
  }
  if (converted&&loaded&&assigned) {


    for (int i = 0; i< rendering.length; i++) {
      textSize(20);
      if (rendering[i]||compiling[i]) {
        if (rendering[i]) {
          text("rendering", 0, 40*i);
        }
        if (compiling[i]) {
          text("compiling", 0, 40*i);
        }
        rectMode(CORNER);
        rect(0, 20+40*i, 150, 20);
        fill(255, 0, 0);
        rect(0, 20+40*i, map((currrender[i]+1.0)/float(numframes[i]), 0, 1, 0, 150), 20);
        textSize(15);
        textAlign(CENTER, CENTER);
        stroke(255);
        fill(50);
        rect(500, 40*i, 88, 40);
        PImage img = loadImage(sketchPath()+"/temp/Images/"+i+"/line_"+("0000000000"+(constrain(currrender[i]-5, 0, 999999999))).substring(((constrain(currrender[i]-5, 0, 999999999))+"").length())+".png");
        if (img!=null) {
          image(img, 500, 40*i, 88, 40);
        }
        stroke(0);
        fill(0);

        rectMode(CENTER);
        text(constrain((int)(100*((currrender[i]+1.0)/float(numframes[i]))), 0, 100)+"%", 75, 28+40*i);
        textAlign(LEFT, TOP);
        fill(255);
      }
      textSize(20);
      if (done[i]) {
        text("DONE!", 0, 0+40*i);
      }
      text(names[i], 150, 40*i);
    }
    textSize(12);
    text("Settings: FPS: "+fps+", WIDTH: "+realw+", TAILLENGTH: "+tl+", BORDER THICKNESS: "+borderThickness+", BGOPACITY: "+backgroundAlpha, 0, height-12);
  }
  }else{text("no Files in /LOG!",0,0);}
  }else{
    text("new version available!",0,0);
  }
}




void calc() {
  if(numf!=0){
  ellipseMode(CENTER);
  rectMode(CENTER);
  int n=val;
  gotit=true;
  File[] logs= new File(sketchPath()+"/temp/csv/").listFiles();

  names[n]=logs[n].getName();
  Table table;
  PGraphics alphaG;

  float space;
  ProcessBuilder processBuilder;
  alphaG = createGraphics(realw, (int)(realw/2), JAVA2D);
  table = loadTable(logs[n]+"", "header");
  int startindex=1;


  float lengthus=(int(table.getRow(table.getRowCount()-1).getString(1).trim())-int(table.getRow(startindex).getString(1).trim()))*0.98879;
  numframes[n] = (int)((lengthus/1000000f)*fps);
  rendering[n]=true;
  int lengthlist = table.getRowCount()-startindex;
  space=float(lengthlist)/numframes[n];
  int where=0;
  for (int i = startindex; i<table.getRowCount(); i+=space) {
    if (exited) {
      return;
    }
    TableRow row = table.getRow(i);

    currrender[n]++;
    alphaG.beginDraw();
    alphaG.translate(xoff, yoff);
    alphaG.clear();
    alphaG.fill(255, 255, 255, 255);
    alphaG.stroke(0, 0, 0, 255);
    alphaG.strokeWeight(w/400);
    //vert
    alphaG.rect(w/2-w/30-w/6-w/400, ((w/3)/7.3)/2, w/200, w/3 );
    alphaG.rect(w/2+w/30+w/6-w/400, ((w/3)/7.3)/2, w/200, w/3 );
    //hori
    alphaG.rect(w/2-w/30-w/3, w/6-w/400+((w/3)/7.3)/2, w/3, w/200);
    alphaG.rect(w/2+w/30, w/6-w/400+((w/3)/7.3)/2, w/3, w/200);

    //text
    alphaG.textSize(w/25);
    if(mode == 2){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }else if(mode ==3){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    
    }if(mode == 1){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }if(mode == 4){
      //throttle
      alphaG.textAlign(LEFT, CENTER);
      alphaG.text((int)map(constrain(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3)/2);
      //pitch
      alphaG.textAlign(RIGHT, CENTER);
      alphaG.text(int(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3)/2);
      //yaw
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(int(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2)-w/75);
      //roll
      alphaG.textAlign(CENTER, BOTTOM);
      alphaG.text(-int(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2)-w/75);
    }
    
    

    alphaG.noStroke();
    float taillength=(fps/30)*tl;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    for (int j = 0; j< space*taillength; j++) {
      TableRow trailrow = table.getRow(constrain(i-j, 0, table.getRowCount()-1));
      alphaG.fill(175, 175, 175, map(j, 0, space*taillength, 100, 0));
      float r = map(j, 0, space*taillength, ((w/3)/7.3)/1.5, ((w/3)/7.3)/10);
      float x=0, y=0, x1=0, y1=0;
      if (mode==2) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (mode==3) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (mode==1) {
        //yaw
        x =map(-int(trailrow.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(int(trailrow.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
      }
      if (mode==4) {
        //yaw
        x =map(int(trailrow.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3);
        //throttle
        y = map(-(int)map(int(trailrow.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2;
        //roll
        x1= map(-int(trailrow.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3);
        //pitch
        y1 = map(-int(trailrow.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2;
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
    if(mode==2||mode==4){
    alphaG.text("Mode"+mode,w/2-w/30-w/6-w/400,w/4);
    }else{
    alphaG.text("Mode"+mode,w/2+w/30+w/6-w/400,w/4);
    }
    alphaG.fill(255, 102, 102, 255);
    if (mode==2) {
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }else if(mode==3){
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    
      }
    else if(mode==1){
       alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }if (mode==4) {
      alphaG.ellipse(map(int(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(int(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
      alphaG.ellipse(map(-int(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-int(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3)/2, (w/3)/7.3, (w/3)/7.3);
    }
    alphaG.endDraw();
    PImage border = createImage(alphaG.width, alphaG.height, ARGB);
    if (borderThickness>0) {
      border.loadPixels();
      for (int j = 0; j<border.pixels.length; j++) {
        border.pixels[j]=0;
        int m=9999;
        for (int x = -borderThickness; x<borderThickness; x++) {
          if(m!=0){
            for (int y = -borderThickness; y<borderThickness; y++) {
              if(m!=0){
                if(((alphaG.pixels[constrain(j+x+(y*alphaG.width),0,border.pixels.length-1)]>>24)&0xFF)!=0){
                  m=(int)min(dist(0,0,x,y),m);
                }
              }
            }
          }
        }
        border.pixels[j] = ((int)(map(m,borderThickness*1.415,0,0,255))<<24);
      }
      border.updatePixels();
    }

    PGraphics out = createGraphics(alphaG.width, alphaG.height, JAVA2D);
    out.beginDraw();
    out.clear();
    if (backgroundAlpha>0) {
      out.background(backgroundColor, backgroundAlpha);
    }
    if (borderThickness>0) {

      out.image(border, 0,0);
    }
    out.image(alphaG, 0, 0);

    out.endDraw();
    out.save("temp/Images/"+n+"/line_"+("0000000000"+where).substring((where+"").length())+".png"); 




    where++;
  }




  rendering[n]=false;
  compiling[n]=true;
  processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe", "-r", fps+"", "-i", sketchPath()+"/temp/Images/"+n+"/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", sketchPath()+"/OUTPUT/"+logs[n].getName().substring(0, logs[n].getName().length()-4)+".mov");
  try {
    Process process = processBuilder.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      if (line.contains("frame")) {
        currrender[n]=Integer.parseInt(line.substring(7, 13).replaceAll("[\\D]", ""));
      }
      println(line);
      line= reader.readLine();
    }
  }
  catch(Exception e) {
  }
  File[] files= new File(sketchPath()+"/temp/Images/"+n+"/").listFiles();

  for (File f : files) {
    f.delete();
  }
  compiling[n]=false;
  done[n]=true;
  }
}
void clearTemp() {
  for (int i = 0; i<val+1; i++) {
    File[] files= new File(sketchPath()+"/temp/Images/"+i+"/").listFiles();
    if (files!=null) {
      for (File f : files) {
        f.delete();
      }
    }
    File f =new File(sketchPath()+"/temp/Images/"+i+"/");
    if (f!=null) {
      f.delete();
    }
  }
  File f = new File(sketchPath()+"/temp/Images/");
  if (f!=null) {
    f.delete();
  }
  File[] files= new File(sketchPath()+"/temp/csv/").listFiles();
  if (files!=null) {
    for (File f2 : files) {
      f2.delete();
    }
  }
  File f2 = new File(sketchPath()+"/temp/csv/");
  if (f2!=null) {
    f2.delete();
  }
  File f3 = new File(sketchPath()+"/temp/");
  if (f3!=null) {
    f3.delete();
  }
}
void exit() {
  exited=true;
  delay(500);
  clearTemp();
  super.exit();
}
void init() {
  createImage(1, 1, RGB).save(sketchPath()+"/LOGS/temp.png");
  createImage(1, 1, RGB).save(sketchPath()+"/OUTPUT/temp.png");
  new File(sketchPath()+"/OUTPUT/temp.png").delete();
  new File(sketchPath()+"/LOGS/temp.png").delete();
  createImage(1, 1, RGB).save(sketchPath()+"/temp/csv/temp.png");
  new File(sketchPath()+"/temp/csv/temp.png").delete();
  final PImage icon = loadImage("assets/icon.png");
  surface.setIcon(icon);
}
void convertLogs() {
  numf = new File(sketchPath()+"/LOGS").listFiles().length;
  if(numf!=0){
  ArrayList<String> args = new ArrayList<String>();
  args.add(sketchPath()+"/assets/blackbox_decode.exe");
  for (int i = 0; i<numf; i++) {
    String name = new File(sketchPath()+"/LOGS").listFiles()[i].getName();
    if (name.contains(".BFL")) {
      args.add(sketchPath()+"/LOGS/"+new File(sketchPath()+"/LOGS").listFiles()[i].getName());
    }
  }
  ProcessBuilder pb = new ProcessBuilder(args);
  try {
    Process process = pb.start();
    InputStream is = process.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line !=null) {
      println(line);

      line= reader.readLine();
    }

    numf = new File(sketchPath()+"/LOGS").listFiles().length;
    for (int i = 0; i<numf; i++) {
      File f = new File(sketchPath()+"/LOGS").listFiles()[i];
      if (f.getName().contains(".event")) {
        f.delete();
        i--;
      }
      if (f.getName().contains(".csv")) {
        f.renameTo(new File(sketchPath()+"/temp/csv/"+f.getName()));
        i--;
      }
    }
  }
  catch(Exception e) {
    e.printStackTrace();
  }}
  converted=true;
  
}
void loadLogs() {
  while (!converted) {
    delay(1);
  }
  if(numf!=0){
  num= new File(sketchPath()+"/temp/csv").listFiles().length-1;
  surface.setResizable(true);
  surface.setSize(width, (60*(num+1))+20);
  xml = loadXML("settings.xml");
  XML[] children = xml.getChildren("setting");
  for (int i = 0; i< children.length; i++) {
    if (children[i].getString("id").equals("fps")) {
      fps = Integer.parseInt(children[i].getContent());
    } else if (children[i].getString("id").equals("width")) {
      realw = Integer.parseInt(children[i].getContent());
      w=(int)(realw*0.98);
      xoff=(realw-w)/2;
      yoff=(int)(realw/2-w/2.2)/2;
    } else if (children[i].getString("id").equals("taillength")) {
      tl = Float.parseFloat(children[i].getContent());
    } else if (children[i].getString("id").equals("borderThickness")) {
      borderThickness = Integer.parseInt(children[i].getContent());
    } else if (children[i].getString("id").equals("backgroundColor")) {
      String s = children[i].getContent();
      backgroundColor = color(Integer.parseInt(s.substring(1, 3), 16), Integer.parseInt(s.substring(3, 5), 16), Integer.parseInt(s.substring(5, 7), 16));
    } else if (children[i].getString("id").equals("backgroundOpacity")) {
      backgroundAlpha = Integer.parseInt(children[i].getContent());
    } else if (children[i].getString("id").equals("sticksMode")) {
      mode = constrain(Integer.parseInt(children[i].getContent()), 1, 4);
    }
  }

  rendering= new boolean[num+1];
  compiling= new boolean[num+1];
  done= new boolean[num+1];
  currrender = new int[num+1];
  numframes = new int[num+1];
  names = new String[num+1];
  }
  loaded=true;
  
}
void assignThreads() {
  while (!loaded) {
    delay(1);
  }
  if(numf!=0){
  val=0;
  int done=0;
  thread("calc");
  while (done<num) {
    if (gotit) {
      gotit=false;
      val++;
      thread("calc");
      done++;
    }
  }
  }
  assigned=true;
}
boolean checkVersion(){
  GetRequest get = new GetRequest("https://api.github.com/repos/bsondermann/BlackboxSticksExporter/releases/latest");
  get.send();
  if(Float.parseFloat(parseJSONObject(get.getContent()).getString("tag_name").trim().substring(1))>ver){return true;}
  else{return false;}
}
