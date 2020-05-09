import java.io.InputStreamReader;

int w = 500;
int fps = 24;
float tl=50;
XML xml;
int val;
int num;
int borderThickness=0;
boolean gotit=false;
boolean[]rendering;
boolean[]compiling;
boolean[]done;
int[] currrender;
int[] numframes;
String[]names;
boolean exited=false;
void setup(){
  size(600,80);
  final PImage icon = loadImage("assets/icon.png");
  surface.setIcon(icon);
  num= new File(sketchPath()+"/LOGS").listFiles().length-1;
  surface.setResizable(true);
  surface.setSize(width,(60*(num+1))+20);
  xml = loadXML("settings.xml");
  XML[] children = xml.getChildren("setting");
  for(int i = 0; i< children.length;i++){
    if(children[i].getString("id").equals("fps")){fps = Integer.parseInt(children[i].getContent());
    }else if(children[i].getString("id").equals("width")){w = Integer.parseInt(children[i].getContent());}
    else if(children[i].getString("id").equals("taillength")){tl = Float.parseFloat(children[i].getContent());
  }else if(children[i].getString("id").equals("borderThickness")){borderThickness = Integer.parseInt(children[i].getContent());
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
void draw(){
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
    rect(0,20+40*i,map((currrender[i]+1.0)/float(numframes[i]),0,1,0,150),20);
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
    text(constrain((int)(100*((currrender[i]+1.0)/float(numframes[i]))),0,100)+"%",75,28+40*i);
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
void calc(){
  
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
  alphaG = createGraphics(w,(int)(w/2.2), JAVA2D);
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
  numframes[n] = (int)((float(lengthus)/1000000f)*fps);
  rendering[n]=true;
  int lengthlist = table.getRowCount()-startindex;
  space=float(lengthlist)/numframes[n];
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
    alphaG.rect(w/2-w/30-w/6-w/400,    ((w/3)/7.3)/2,              w/200,    w/3 );
    alphaG.rect(w/2+w/30+w/6-w/400,    ((w/3)/7.3)/2,              w/200,    w/3 );
    //hori
    alphaG.rect(w/2-w/30-w/3,              w/6-w/400+((w/3)/7.3)/2,        w/3,    w/200);
    alphaG.rect(w/2+w/30,                      w/6-w/400+((w/3)/7.3)/2,        w/3,    w/200);
    
    //text
    alphaG.textSize(w/25);
    alphaG.textAlign(LEFT,CENTER);
    alphaG.text((int)map(constrain(-(int)map(row.getInt(16),1000,2000,-500,500),-500,500),-500,500,2000,1000)+"",w/75,w/6-w/200+((w/3)/7.3)/2);
    alphaG.textAlign(RIGHT,CENTER);
    alphaG.text(row.getInt(14)+"",w-w/75,w/6-w/200+((w/3)/7.3)/2);
    alphaG.textAlign(CENTER,BOTTOM);
    alphaG.text(-row.getInt(15)+"",w/2-w/30-w/6-w/400,alphaG.height-w/75);
    alphaG.textAlign(CENTER,BOTTOM);
    alphaG.text(row.getInt(13)+"",w/2+w/30+w/6-w/400,alphaG.height-w/75);
    

    alphaG.noStroke();
    float taillength=(fps/30)*tl;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    
    for(int j = 0; j< space*taillength;j++){
      TableRow trailrow = table.getRow(constrain(i-j,0,table.getRowCount()-1));
          alphaG.fill(175,175,175,map(j,0,space*taillength,100,0));
          float r = map(j,0,space*taillength,((w/3)/7.3)/1.5,((w/3)/7.3)/10);
          float x =map(-trailrow.getInt(15),-500,500,w/2-w/30-w/3,w/2-w/30-w/3+w/3);
          float y = map(-(int)map(trailrow.getInt(16),1000,2000,-500,500),-500,500,0,w/3)+((w/3)/7.3)/2;
          float x1= map(trailrow.getInt(13),-500,500,w/2+w/30,w/2+w/30+w/3);
          float y1 = map(-trailrow.getInt(14),-500,500,0,w/3)+((w/3)/7.3)/2;
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
    alphaG.ellipse(map(-row.getInt(15),-500,500,w/2-w/30-w/3,w/2-w/30-w/3+w/3),map(-(int)map(row.getInt(16),1000,2000,-500,500),-500,500,0,w/3)+((w/3)/7.3)/2,(w/3)/7.3,(w/3)/7.3);
    alphaG.ellipse(map(row.getInt(13),-500,500,w/2+w/30,w/2+w/30+w/3),map(-row.getInt(14),-500,500,0,w/3)+((w/3)/7.3)/2,(w/3)/7.3,(w/3)/7.3);
    alphaG.endDraw();   
    if(borderThickness>0){
    PImage border = createImage(alphaG.width,alphaG.height,ARGB);

    border.loadPixels();
    for(int j = 0; j<border.pixels.length;j++){
      border.pixels[j]=0;
      for(int x = -borderThickness;x<borderThickness;x++){
        for(int y = -borderThickness;y<borderThickness;y++){
          if(alpha(alphaG.pixels[constrain(x+(j%alphaG.width),0,alphaG.width-1)+constrain(y+(j/alphaG.width),0,alphaG.height-1)*alphaG.width])!=0){
            border.pixels[j]=-16777216;
          }
        }
      }
     
        
    }
    border.updatePixels();
    
  PGraphics out = createGraphics(alphaG.width,alphaG.height,JAVA2D);
    out.beginDraw();
    out.clear();
    out.image(border,-0.5,-0.5);
    out.filter(BLUR,borderThickness/2);
    
    out.image(alphaG,0,0);
    
    out.endDraw();
    out.save("tempImages/"+n+"/line_"+("0000000000"+where).substring((where+"").length())+".png"); 
    }else{
      alphaG.save("tempImages/"+n+"/line_"+("0000000000"+where).substring((where+"").length())+".png"); 
    }

 
  
   where++;
  }
 
    
  

    

  
  rendering[n]=false;
  compiling[n]=true;
  processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe","-r",fps+"","-i",sketchPath()+"/tempImages/"+n+"/line_%010d.png","-vcodec","prores_ks","-pix_fmt","yuva444p10le","-profile:v","4444","-q:v","30","-r",fps+"","-y",sketchPath()+"/OUTPUT/"+logs[n].getName().substring(0,logs[n].getName().length()-4)+".mov");
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

void exit() {
  exited=true;
  delay(500);
  for(int i = 0; i<val+1; i++){
  File[] files= new File(sketchPath()+"/tempImages/"+i+"/").listFiles();
  
  for(File f:files){
    f.delete();
  }
  new File(sketchPath()+"/tempImages/"+i+"/").delete();
  }
  File f = new File(sketchPath()+"/tempImages/");
  if(f!=null){
  f.delete();
  }
  
  super.exit();
}
