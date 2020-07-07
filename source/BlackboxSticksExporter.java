import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.nio.file.Files; 
import http.requests.*; 
import processing.sound.*; 
import java.io.PrintStream; 
import java.io.FileOutputStream; 
import java.util.LinkedList; 
import drop.*; 
import java.awt.Color; 
import java.net.URL; 
import java.awt.Desktop; 
import processing.event.*; 
import processing.core.*; 
import processing.event.*; 
import java.awt.datatransfer.*; 
import java.awt.Toolkit; 
import processing.core.*; 
import processing.event.*; 
import processing.event.*; 
import java.lang.reflect.*; 
import processing.core.*; 
import processing.core.*; 
import processing.event.*; 
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








Selection s;
File inputPath,outputPath;
LinkedList<File> inputFiles = new LinkedList<File>();
int inputFilesNum=0;
XML data;
RenderManager rm;
float version;
boolean newVersion=false;
Language lang;
SDrop drop;
public void setup(){
  
  final PImage icon = loadImage("assets/icon.png");
  surface.setIcon(icon);
  drop = new SDrop(this);
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
  lang = new Language(data.getChildren("data")[4].getContent());

  
  if(!newVersion){
    s=new Selection(this);
  }
  thread("clearTemp");
  frameRate(30);
}
public void draw(){
  
  background(50);
  if(!newVersion){
    s.show();
    if(s.active()){
    if(inputPath!=null){
      String text=inputPath.getAbsolutePath()+" | "+inputFilesNum+" "+lang.getTranslation("infoNumLogsFound");
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(text)+20)/2,58,textWidth(text)+20,22);
      fill(0);
      text(text,400,76);
    }
    if(inputPath==null&&inputFiles.size()>0){
      String text=inputFiles.size()+" "+lang.getTranslation("infoNumLogsFound");
      textAlign(CENTER,BOTTOM);
      stroke(0);
      fill(100);
      rect(400-(textWidth(text)+20)/2,58,textWidth(text)+20,22);
      fill(0);
      text(text,400,76);
    }
    if(outputPath!=null){      
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
    text(lang.getTranslation("newVersionAlert"),width/2,height/2);
    
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
    if(files[i].getName().contains(ending)||files[i].getName().contains(".bbl")){
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
  if(newVersion){
    try{
    Desktop.getDesktop().browse(new URL("https://www.github.com/bsondermann/BlackboxSticksExporter/releases/").toURI());
    }catch(Exception e){e.printStackTrace();}
  }
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
public void dropEvent(DropEvent e){
  if(e.isFile()){
    File f = e.file();
    if(f.isDirectory()){
      inputPath=f;
      inputFilesNum=getNumFiles(inputPath,"BFL");
      
      data.getChildren("data")[0].setContent(f.getAbsolutePath());
      saveXML(data,"assets/data.xml");
    }else{
      if(f.getAbsolutePath().contains(".BFL")||f.getAbsolutePath().contains(".bbl")){
        boolean contains = false;
        for(File fi : inputFiles){
          if(fi.getAbsolutePath().equals(f.getAbsolutePath())){
            contains=true;
          }
        }
        if(!contains){
          inputFiles.add(f);
          inputPath=null;
          
        }
      }
    }
  }
}
public void savePreset(File f){
  if(s!=null){
    s.savePreset(f);
  }
}
public void loadPreset(File f){
  if(s!=null){
    s.loadPreset(f);
  }
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
  boolean prevrectSelected=false;
  boolean prevringSelected=false;
  public void show(){
    if(rectSelected){
        posSelRect.x= constrain(map(mouseX,posx+w/2-rect.width/2,posx+w/2+rect.width/2,0,1),0,1);
        posSelRect.y =constrain( map(mouseY,posy+w/2-rect.height/2,posy+w/2+rect.height/2,0,1),0,1);
        
        prevrectSelected=true;
    }
    if(ringSelected){
      posSelRing = PVector.fromAngle(new PVector(mouseX-(posx+w/2),mouseY-(posy+w/2)).heading());
      prevringSelected=true;
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
    calcRect();
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
  }
  public String getHexColor(){
    return hex(getColor()).substring(2);
  }
}


class Credits{
  Selection sele;
  GUIController cont;
  IFButton backbtn;
  boolean active = false;
  PImage icomail,icoinsta,icoyt;
  PApplet app;
  String[][] data={
    {"Name","Job","Role"},
  {"Bastian Sondermann","Developer","Developing / Testing / German translation","http://www.instagram.com/bsondermann581/","bastian.sondermann@web.de"},
  {"Fabio Pansera","Video Editor","Testing / Italian, Ukrainian, Russian translation","http://www.instagram.com/thelollerz/","fabiopanseratcb@gmail.com"},
  {"Joshua Bardwell","FPV Know-It-All","Testing","http://www.youtube.com/joshuabardwell","joshuabardwell@gmail.com"}
  };
  Credits(Selection s,PApplet applet){
    sele=s;
    app = applet;
    cont = new GUIController(app);
    createSettingsGui();
    icomail = loadImage(sketchPath()+"/assets/email.png");
    icoinsta = loadImage(sketchPath()+"/assets/ig.png");
    icoyt = loadImage(sketchPath()+"/assets/yt.png");
    
  }
  public void show(){
    if(this.active){
      imageMode(CORNER);
      textAlign(TOP,LEFT);
      fill(100);
      rect(40,70,720,30*(data.length)+10);
      fill(0);
      for(int i = 0; i< data.length; i++){
        if(i==0){
          textSize(15);
        }else{
          textSize(13);
        }
        for(int j = 0; j<3;j++){
          text(data[i][j],50+(720/5)*j,90+30*i);
        }
        if(i!=0){
            if(data[i][3].contains("instagram")){
            image(icoinsta,700,73+30*i);}
            if(data[i][3].contains("youtube")){
            image(icoyt,700,78+30*i);}
            if(data[i][4].contains("@")){
            image(icomail,730,76+30*i);}
           }
        }
      }
      if(cont != null){cont.show();}
    }
  
  public void mousePress(){
    
    if(this.active){
    for(int i = 1; i< data.length;i++){
      if(mouseX>700&&mouseX<725&&mouseY>73+30*i&&mouseY<73+30*i+25){
        try{
          Desktop.getDesktop().browse(new URL(data[i][3]).toURI());
        }catch(Exception e){e.printStackTrace();}
        sele.console = data[i][3];
      }
      if(mouseX>730&&mouseX<755&&mouseY>76+30*i&&mouseY<76+30*i+25){
        try{
          Desktop.getDesktop().mail(new URL("mailto:"+data[i][4]).toURI());
        }catch(Exception e){e.printStackTrace();}
        sele.console = data[i][4];
      }
    }}
  }
   public void actionPerformed(GUIEvent e){
    if(this.active){
    if(e.getSource()==backbtn){
      
      this.removeSettingsGui();
    }
    }
  }
  public void createSettingsGui(){
   backbtn = new IFButton(lang.getTranslation("btnBack"),50,30,100,20);
    backbtn.addActionListener(app);
    this.cont.add(backbtn);
    this.active=true;
  }
  
  public void removeSettingsGui(){

    cont.remove(backbtn);
    backbtn.addActionListener(null);
    backbtn=null;
    cont.setVisible(false);
    cont=null;
    active = false;
    sele.createSelectionGui();
  }
}


 abstract class GUIComponent {
  private int x, y, wid, hgt;
  private String label;
  
  // TODO Can I get rid of this?
  protected boolean wasClicked = false;
  
  protected Object listener;
  protected IFLookAndFeel lookAndFeel;
  protected GUIController controller;
  //protected PFont meta;
  // May not need this
  protected int index;

  
  public GUIComponent () {
  }
  
  public void setIndex(int i) {
    index = i;
  }
  
  public int getIndex() {
    return index;
  }
  
  public void update(int argX, int argY) {
  }
  
  public void show() {
  }
  
  public void setController (GUIController c) {
    controller = c;
  }
  
  public GUIController getController() {
    return controller;
  }
  
  public void initWithParent () {
  }
  
  public void setLookAndFeel(IFLookAndFeel lf) {
    lookAndFeel = lf;
  }
  
  public IFLookAndFeel getLookAndFeel() {
    return lookAndFeel;
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel (String argLabel) {
    label = argLabel;
  }
  
  public boolean canReceiveFocus() {
    return true;
  }
  
  public int getWidth() {
    return wid;
  }
  
  public void setWidth(int newWidth) {
    if (newWidth > 0) wid = newWidth;
  }
  
  public int getHeight() {
    return hgt;
  }
  
  public void setHeight(int newHeight) {
    if (newHeight > 0) hgt = newHeight;
  }
  
  public void addActionListener (Object newListener) {
    listener = newListener;
  }
  
  public void setSize(int newWidth, int newHeight) {
    if (newHeight > 0 && newWidth > 0) {
      hgt = newHeight;
      wid = newWidth;
    }
  }
  
  //public IFSize getSize() {
  //  return new IFRect(wid, hgt);
  //}
  
  public void setPosition(int newX, int newY) {
    if (newX >= 0 && newY >= 0) {
      x = newX;
      y = newY;
    }
  }
  
  //public IFPoint getPosition() {
  //  return new IFPoint(x, y);
  //}
    
  public void setX(int newX) {
    if (newX >= 0) x = newX;
  }

  public int getX() {
    return x;
  }
  
  public int getAbsoluteX() {
    if (controller != null) 
      return controller.getAbsoluteX() + x;
    else
      return x;
  }

  public void setY(int newY) {
    if (newY >= 0) y = newY;
  }
  
  public int getY() {
    return y;
  }
  
  public int getAbsoluteY() {
    if (controller != null)
      return controller.getAbsoluteY() + y;
    else
      return y;
  }

  public void mouseEvent (MouseEvent e) {
    if (e.getAction() == MouseEvent.PRESS) {
      if (isMouseOver (e.getX(), e.getY())) {
         wasClicked = true;
      }
    } else if (e.getAction() == MouseEvent.RELEASE) {
      if (wasClicked && isMouseOver (e.getX(), e.getY())) {
         fireEventNotification(this, "Clicked");
         wasClicked = false;
      }
    }
  }
  
  public void keyEvent (KeyEvent e) {
  }
  
  public void actionPerformed (GUIEvent e) {
    
  }

  
  public void fireEventNotification (GUIComponent argComponent, String argMessage) {
    if (listener == null)
      return;
    
    GUIEvent e = new GUIEvent(argComponent, argMessage);
    IFDelegation.callDelegate(listener, "actionPerformed", new Object[] { e });
      
  }
  
  public boolean isMouseOver (int mouseX, int mouseY) {
    return ((mouseX >= x) && (mouseY >= y) && (mouseX <= (x + wid)) && (mouseY <= (y + hgt)));
  }
  
}
public class GUIEvent {
   GUIComponent source;
   String message;

  GUIEvent (GUIComponent argSource, String argMessage) {
     source = argSource;
    message = argMessage;
   }

  public GUIComponent getSource() {
     return source;
   }

   public String getMessage() {
    return message;
   }

}






public class GUIController extends GUIComponent implements ClipboardOwner {
  private GUIComponent[] contents;
  private int numItems = 0;
  private int focusIndex = -1;
  private boolean visible;
  private IFLookAndFeel lookAndFeel;
  public IFPGraphicsState userState;
  private Clipboard clipboard;

  public PApplet parent;

  public boolean showBounds = false;
  
  public GUIController (PApplet newParent) {
    this(newParent, true);
  }
  
  public GUIController (PApplet newParent, int x, int y, int width, int height) {
    this(newParent, true);
    setPosition(x, y);
    setSize(width, height);
  }

  public GUIController (PApplet newParent, boolean newVisible) {
    setParent(newParent);
    setVisible(newVisible);
    contents = new GUIComponent[5];
    
    lookAndFeel = new IFLookAndFeel(parent, IFLookAndFeel.DEFAULT);
    userState = new IFPGraphicsState();
    
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      try {
        security.checkSystemClipboardAccess();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      } catch (SecurityException e) {
        clipboard = new Clipboard("Interfascia Clipboard");
      }
    } else {
      try {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      } catch (Exception e) {
        // THIS IS DUMB
      }
    }
    
    newParent.registerMethod("keyEvent", this);
    newParent.registerMethod("show", this);
  }
  
  public void setLookAndFeel(IFLookAndFeel lf) {
    lookAndFeel = lf;
  }
  
  public IFLookAndFeel getLookAndFeel() {
    return lookAndFeel;
  }

  public void add (GUIComponent component) {
    if (numItems == contents.length) {
      GUIComponent[] temp = contents;
      contents = new GUIComponent[contents.length * 2];
      System.arraycopy(temp, 0, contents, 0, numItems);
    }
    component.setController(this);
    component.setLookAndFeel(lookAndFeel);
    //component.setIndex(numItems);
    contents[numItems++] = component;
    component.initWithParent();
  }

  public void remove (GUIComponent component) {
    int componentIndex = -1;
    
    for (int i = 0; i < numItems; i++) {
      if (component == contents[i]){
        componentIndex = i;
        break;
      }
    }
    
    if (componentIndex != -1) {
      contents[componentIndex] = null;
      if (componentIndex < numItems - 1) {
        System.arraycopy(contents, componentIndex + 1, contents, componentIndex, numItems - (componentIndex + 1));
      }
      numItems--;
    }
  }
  
  public void setParent (PApplet argParent) {
    parent = argParent;
  }
  
  public PApplet getParent () {
    return parent;
  }
  
  public void setVisible (boolean newVisible) {
    visible = newVisible;
  }
  
  public boolean getVisible() {
    return visible;
  }
  
  public void requestFocus(GUIComponent c) {
    for (int i = 0; i < numItems; i++) {
      if (c == contents[i])
        focusIndex = i;
    }
  }
  
  // ****** LOOK AT THIS, I DON'T THINK IT'S RIGHT ******
  public void yieldFocus(GUIComponent c) {
    if (focusIndex > -1 && focusIndex < numItems && contents[focusIndex] == c) {
      focusIndex = -1;
    }
  }
  
  public GUIComponent getComponentWithFocus() {
    return contents[focusIndex];
  }
  
  public boolean getFocusStatusForComponent(GUIComponent c) {
    if (focusIndex >= 0 && focusIndex < numItems)
      return c == contents[focusIndex];
    else
      return false;
  }



  public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
    // System.out.println ("Lost ownership");
  }
  
  public void copy(String v)
  {
    StringSelection fieldContent = new StringSelection (v);
    clipboard.setContents (fieldContent, this);
  }
  
  public String paste()
  {
    Transferable clipboardContent = clipboard.getContents (this);
    
    if ((clipboardContent != null) &&
      (clipboardContent.isDataFlavorSupported (DataFlavor.stringFlavor))) {
      try {
        String tempString;
        tempString = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
        return tempString;
      }
      catch (Exception e) {
        e.printStackTrace ();
      }
    }
    return "";
  }
  


  public void keyEvent(KeyEvent e) {
    if (visible) {
      if (e.getAction() == KeyEvent.PRESS && e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
        if (focusIndex != -1 && contents[focusIndex] != null) {
          contents[focusIndex].actionPerformed(
            new GUIEvent(contents[focusIndex], "Lost Focus")
          );
        }
        
        if (e.isShiftDown())
          giveFocusToPreviousComponent();
        else
          giveFocusToNextComponent();
        
        if (focusIndex != -1 && contents[focusIndex] != null) {
          contents[focusIndex].actionPerformed(
            new GUIEvent(contents[focusIndex], "Received Focus")
          );
        }

      } else if (e.getKeyCode() != java.awt.event.KeyEvent.VK_TAB) {
        if (focusIndex >= 0 && focusIndex < contents.length)
          contents[focusIndex].keyEvent(e);
      }
    }
  }
  
  private void giveFocusToPreviousComponent() {
    int oldFocus = focusIndex;
    focusIndex = (focusIndex - 1) % numItems;
    while (!contents[focusIndex].canReceiveFocus() && focusIndex != oldFocus) {
      focusIndex = (focusIndex - 1) % numItems;
    }
  }
  
  private void giveFocusToNextComponent() {
    int oldFocus = focusIndex;
    focusIndex = (focusIndex + 1) % numItems;
    while (!contents[focusIndex].canReceiveFocus() && focusIndex != oldFocus) {
      focusIndex = (focusIndex + 1) % numItems;
    }
  }

  public void show() {
    if (visible) {
      userState.saveSettingsForApplet(parent);
      lookAndFeel.defaultGraphicsState.restoreSettingsToApplet(parent);
      //parent.background(parent.g.backgroundColor);
      parent.fill(color(0));
      parent.rect(getX(), getY(), getWidth(), getHeight());
      for(int i = 0; i < contents.length; i++){
        if(contents[i] != null){
          //parent.smooth();
          contents[i].show();
        }
      }
      userState.restoreSettingsToApplet(parent);   
    }
  }   
}



public class IFButton extends GUIComponent {
  private int currentColor;

  public IFButton (String newLabel, int newX, int newY) {
    this (newLabel, newX, newY, 100, 21);
  }

  public IFButton (String newLabel, int newX, int newY, int newWidth) {
    this (newLabel, newX, newY, newWidth, 21);
  }

  public IFButton (String newLabel, int newX, int newY, int newWidth, int newHeight) {
    setLabel(newLabel);
    setPosition(newX, newY);
    setSize(newWidth, newHeight);
  }

  public void initWithParent () {
    controller.parent.registerMethod("mouseEvent", this);
  }

  public void mouseEvent(MouseEvent e) {
    if (e.getAction() == MouseEvent.PRESS) {
      if (isMouseOver (e.getX(), e.getY())) {
        wasClicked = true;
      }
    } else if (e.getAction() == MouseEvent.RELEASE) {
      if (wasClicked && isMouseOver (e.getX(), e.getY())) {
        fireEventNotification(this, "Clicked");
        wasClicked = false;
      }
    }
  }

  public void show () {
    boolean hasFocus = controller.getFocusStatusForComponent(this);
  
    if (wasClicked) {
       currentColor = lookAndFeel.activeColor;
    } else if (isMouseOver (controller.parent.mouseX, controller.parent.mouseY) || hasFocus) {
       currentColor = lookAndFeel.highlightColor;
    } else {
       currentColor = lookAndFeel.baseColor;
    }

    int x = getX(), y = getY(), hgt = getHeight(), wid = getWidth();
  
    controller.parent.stroke(lookAndFeel.borderColor);
    controller.parent.fill(currentColor);
    controller.parent.rect(x, y, wid, hgt);
    controller.parent.fill (lookAndFeel.textColor);

    controller.parent.textAlign (PApplet.CENTER);
    controller.parent.text (getLabel(), x, y + 3, wid, hgt);
    controller.parent.textAlign (PApplet.LEFT);

    if (controller.showBounds) {
      controller.parent.noFill();
      controller.parent.stroke(255,0,0);
      controller.parent.rect(x, y, wid, hgt);
    }
  }
    
  public void keyEvent(KeyEvent e) {
    if (e.getAction() == KeyEvent.TYPE && e.getKey() == ' ') {
      fireEventNotification(this, "Selected");
    }
  }

}



public class IFCheckBox extends GUIComponent {
  private int currentColor;
  private boolean selected = false;

  public IFCheckBox (String newLabel, int newX, int newY) {
    setLabel(newLabel);
    setPosition(newX, newY);
    setSize(14, 14);
  }

  public void initWithParent () {
    controller.parent.registerMethod("mouseEvent", this);
    
    if (lookAndFeel == null)
      return;
    
    controller.userState.saveSettingsForApplet(controller.parent);
    lookAndFeel.defaultGraphicsState.restoreSettingsToApplet(controller.parent);
    
    setSize((int) Math.ceil(controller.parent.textWidth(getLabel())) + getHeight() + 5, 14);

    controller.userState.restoreSettingsToApplet(controller.parent);
  }

  public void mouseEvent (MouseEvent e) {
    if (e.getAction() == MouseEvent.PRESS) {
      if (isMouseOver (e.getX(), e.getY())) {
         wasClicked = true;
      }
    } else if (e.getAction() == MouseEvent.RELEASE) {
      if (wasClicked && isMouseOver (e.getX(), e.getY())) {
        if (selected) {
          selected = false;
          fireEventNotification(this, "Unchecked");
        } else {
          selected = true;
          fireEventNotification(this, "Checked");
        }
        wasClicked = false;
      }
    }
  }
  
  public void keyEvent(KeyEvent e) {
    if (e.getAction() == KeyEvent.TYPE && e.getKey() == ' ') {
      fireEventNotification(this, "Selected");
      if (selected) {
        selected = false;
        fireEventNotification(this, "Unchecked");
      } else {
        selected = true;
        fireEventNotification(this, "Checked");
      }
    }
  }

  public void show () {
    if (isMouseOver (controller.parent.mouseX, controller.parent.mouseY)) {
      currentColor = lookAndFeel.highlightColor;
    } else if (controller.getFocusStatusForComponent(this)) {
      currentColor = lookAndFeel.highlightColor;
    } else {
      currentColor = lookAndFeel.baseColor;
    }

    int x = getX(), y = getY(), hgt = getHeight(), wid = getWidth();
    controller.parent.fill(100);
    controller.parent.stroke(0);
    controller.parent.rect(x+hgt+3,y-3,-textWidth(getLabel())-13-hgt,hgt+6);
    controller.parent.stroke(lookAndFeel.borderColor);
    controller.parent.fill(currentColor);
    controller.parent.rect(x, y, hgt, hgt);
    if (selected == true) {
      controller.parent.stroke (lookAndFeel.darkGrayColor);
      controller.parent.line (x + 3, y + 2, hgt + x - 3, hgt + y - 4);
      controller.parent.line (x + 3, y + 3, hgt + x - 4, hgt + y - 4);
      controller.parent.line (x + 4, y + 2, hgt + x - 3, hgt + y - 5);
      controller.parent.line (x + 3, hgt + y - 4, hgt + x - 3, y + 2);
      controller.parent.line (x + 4, hgt + y - 4, hgt + x - 3, y + 3);
      controller.parent.line (x + 3, hgt + y - 5, hgt + x - 4, y + 2);
    }
    
    controller.parent.fill (lookAndFeel.textColor);
    controller.parent.textAlign(RIGHT);
    controller.parent.text (getLabel(), x - 5, (hgt - 2) + y);
    
    if (controller.showBounds) {
      controller.parent.noFill();
      controller.parent.stroke(255,0,0);
      controller.parent.rect(x, y, wid, hgt);
    }
  }

  public boolean isSelected() {
    return selected;
  }

}


static class IFDelegation {
  public static Object callDelegate(Object delegate, String method, Object[] parameters) {
    Method m;
    Object o = null;
    Class[] parameterTypes = new Class[parameters.length];
    
    for (int i = 0; i < parameters.length; i++) {
      parameterTypes[i] = parameters[i].getClass();
    }
    
    try {
      m = delegate.getClass().getDeclaredMethod(method, parameterTypes);
      o = m.invoke(delegate, parameters);
    } catch (NoSuchMethodException e) {
    } catch (IllegalAccessException e) {
    } catch (InvocationTargetException e) {
    }
    
    return o;
  }
}


public class IFLabel extends GUIComponent {
  private int textSize = 13;

  public IFLabel (String argLabel, int argX, int argY) {
    this (argLabel, argX, argY, 13);
  }
  
  public IFLabel (String newLabel, int newX, int newY, int size) {
    setLabel(newLabel);
    setPosition(newX, newY);
    
    if (size > 8 && size < 20) 
      textSize = size;
    else
      textSize = 13;
  }

  // ***** SET THE LABEL'S SIZE SO WE CAN GET ITS BOUNDING BOX *****
  
  public boolean canReceiveFocus() {
    return false;
  }
  
  public void setTextSize(int size) {
    if (size > 8 && size < 20) 
      textSize = size;
    else
      textSize = 13;
  }
  
  public int getTextSize() {
    return textSize;
  }

  public void show () {
    controller.parent.fill (lookAndFeel.textColor);
    controller.parent.text (getLabel(), getX(), getY() + textSize - 3);
  }

}


 class IFLookAndFeel {
   int baseColor, borderColor, highlightColor, selectionColor, 
        activeColor, textColor, lightGrayColor, darkGrayColor;
   IFPGraphicsState defaultGraphicsState;
   static final char DEFAULT = 1;
  
   IFLookAndFeel(char type) {
    defaultGraphicsState = new IFPGraphicsState();
  }
  
   IFLookAndFeel(PApplet parent, char type) {
    defaultGraphicsState = new IFPGraphicsState();
    
    if (type == DEFAULT) {
      // Play nicely with other people's draw methods. They
      // may have changed the color mode.
      IFPGraphicsState temp = new IFPGraphicsState(parent);
      
      parent.colorMode(PApplet.RGB, 255);

      baseColor = color(153, 153, 204);
      highlightColor = color(102, 102, 204);
      activeColor = color (255, 153, 51);
      selectionColor = color (255, 255, 0);
      borderColor = color (255);
      textColor = color (0);
      lightGrayColor = color(100);
      darkGrayColor = color(50);

      /*
      System.out.println("===== DEFAULT GRAPHICS STATE =====\ntextAlign:\t" + parent.g.textAlign +
          "\nrectMode:\t" + parent.g.rectMode +
          "\nellipseMode:\t" + parent.g.ellipseMode +
          "\ncolorMode:\t" + parent.g.colorMode + ", " + parent.g.colorModeX + 
          "\nsmooth:\t" + parent.g.smooth);
      */
      
      PFont tempFont = parent.createFont ("Arial",12);
      parent.textFont(tempFont, 13);
      parent.textAlign(PApplet.LEFT);
      
      parent.rectMode(PApplet.CORNER);
      parent.ellipseMode(PApplet.CORNER);
      
      parent.strokeWeight(1);
      
      parent.colorMode(PApplet.RGB, 255);
      
      try {
        parent.smooth();
      } catch (RuntimeException e) {
        // Can't smooth in P3D, throws exception
      }

      /*
      System.out.println("\n===== INTERFASCIA SETUP ======\ntextAlign:\t" + parent.g.textAlign +
          "\nrectMode:\t" + parent.g.rectMode +
          "\nellipseMode:\t" + parent.g.ellipseMode +
          "\ncolorMode:\t" + parent.g.colorMode + ", " + parent.g.colorModeX +
          "\nsmooth:\t" + parent.g.smooth);
      */
      
      defaultGraphicsState.saveSettingsForApplet(parent);
      // System.out.println("Class: " + parent.g.getClass() + "/n");
      // Set the color mode back
      temp.restoreSettingsToApplet(parent);
    }
  }
}

 class IFPGraphicsState {
  int smooth;
  
  int rectMode, ellipseMode;

  PFont textFont;
  int textAlign;
  float textSize;
  int textMode;
  
  boolean tint;
  int tintColor;
  boolean fill;
  int fillColor;
  boolean stroke;
  int strokeColor;
  float strokeWeight;
  
  int cMode;
  float cModeX, cModeY, cModeZ, cModeA;
  
  IFPGraphicsState() {
  }
  
  
  /**
  * Convenience contstructor saves the applet's graphics state into
  * the newly created IFPGraphicsState object.
  *
  * @param applet the PApplet instance whose state we're saving
  */
  IFPGraphicsState(PApplet applet) {
    saveSettingsForApplet(applet);
  }
  
  
  /**
  * saves the graphics state for the specified PApplet
  *
  * @param applet the PApplet instance whose state we're saving
  */
  
   public void saveSettingsForApplet(PApplet applet) {
    smooth = applet.g.smooth;
    
    rectMode = applet.g.rectMode;
    ellipseMode = applet.g.ellipseMode;
    
    textFont = applet.g.textFont;
    textAlign = applet.g.textAlign;
    textSize = applet.g.textSize;
    textMode = applet.g.textMode;
    
    tint = applet.g.tint;
    fill = applet.g.fill;
    stroke = applet.g.stroke;
    tintColor = applet.g.tintColor;
    fillColor = applet.g.fillColor;
    strokeColor = applet.g.strokeColor;
    strokeWeight = applet.g.strokeWeight;
    cMode = applet.g.colorMode;
    cModeX = applet.g.colorModeX;
    cModeY = applet.g.colorModeY;
    cModeZ = applet.g.colorModeZ;
    cModeA = applet.g.colorModeA;
  }

  
  /**
  * restores the saved graphics state to the specified PApplet
  *
  * @param applet the PApplet instance whose state we're restoring
  */
  
  public void restoreSettingsToApplet(PApplet applet)
  { 

    try {
      if (smooth > 0) {
        applet.smooth();
      } else {
        applet.noSmooth();
      }
    } catch (RuntimeException e) {
      // Can't smooth in P3D, throws exception
    }
    
    applet.rectMode(rectMode);
    applet.ellipseMode(ellipseMode);
    
    if(textFont != null){ 
      applet.textFont(textFont);
      applet.textSize(textSize);
    }
    applet.textAlign(textAlign);
    applet.textMode(textMode);
    
    // ***** I THINK YOU CAN SET A COLOR FOR A PROPERTY THAT'S NOT ENABLED *****
    if(tint) applet.tint(tintColor);
    else applet.noTint();
    
    if(fill) applet.fill(fillColor);
    else applet.noFill();
    
    if(stroke) applet.stroke(strokeColor);
    else applet.noStroke();
    
    applet.strokeWeight(strokeWeight);
    applet.colorMode(cMode, cModeX, cModeY, cModeZ, cModeA);    
  }
  
}



/** The IFTextField class is used for a simple one-line text field */

public class IFTextField extends GUIComponent {

  private String contents = "";
  private int cursorPos = 0;
  private int visiblePortionStart = 0, visiblePortionEnd = 0;
  private int startSelect = -1, endSelect = -1;
  private float cursorXPos = 0, startSelectXPos = 0, endSelectXPos = 0;

  /**
  * creates an empty IFTextField with the specified label, with specified position, and a default width of 100 pixels.
  * @param argLabel the text field's label
  * @param argX the text field's X location on the screen, relative to the PApplet.
  * @param argY the text filed's Y location on the screen, relative 
  * to the PApplet.
  */
  
  public IFTextField (String newLabel, int newX, int newY) {
    this (newLabel, newX, newY, 100, "");
  }


  /**
  * creates an empty IFTextField with the specified label and with specified position and width.
  * @param argLabel the text field's label
  * @param argX the text field's X location on the screen, relative to the PApplet.
  * @param argY the text filed's Y location on the screen, relative to the PApplet.
  * @param argWidth the text field's width
  */
  
  public IFTextField (String argLabel, int argX, int argY, int argWidth) {
    this (argLabel, argX, argY, argWidth, "");
  }


  /**
  * creates an IFTextField with the specified label, with specified position and width, and with specified contents.
  * @param argLabel the text field's label
  * @param argX the text field's X location on the screen, relative to the PApplet.
  * @param argY the text filed's Y location on the screen, relative to the PApplet.
  * @param argWidth the text field's width
  * @param argContents the default contents of the text field
  */
  
  public IFTextField (String argLabel, int argX, int argY, int argWidth, String newValue) {
    setLabel(argLabel);
    setPosition(argX, argY);
    setSize(argWidth, 21);
    setValue(newValue);
  }
  

  public boolean validUnicode(char b)
  {
    int c = (int)b;
    return (
        (c >= 0x0020 && c <= 0x007E) ||
        (c >= 0x00A1 && c <= 0x017F) ||
        (c == 0x018F) ||
        (c == 0x0192) ||
        (c >= 0x01A0 && c <= 0x01A1) ||
        (c >= 0x01AF && c <= 0x01B0) ||
        (c >= 0x01D0 && c <= 0x01DC) ||
        (c >= 0x01FA && c <= 0x01FF) ||
        (c >= 0x0218 && c <= 0x021B) ||
        (c >= 0x0250 && c <= 0x02A8) ||
        (c >= 0x02B0 && c <= 0x02E9) ||
        (c >= 0x0300 && c <= 0x0345) ||
        (c >= 0x0374 && c <= 0x0375) ||
        (c == 0x037A) ||
        (c == 0x037E) ||
        (c >= 0x0384 && c <= 0x038A) ||
        (c >= 0x038E && c <= 0x03A1) ||
        (c >= 0x03A3 && c <= 0x03CE) ||
        (c >= 0x03D0 && c <= 0x03D6) ||
        (c >= 0x03DA) ||
        (c >= 0x03DC) ||
        (c >= 0x03DE) ||
        (c >= 0x03E0) ||
        (c >= 0x03E2 && c <= 0x03F3) ||
        (c >= 0x0401 && c <= 0x044F) ||
        (c >= 0x0451 && c <= 0x045C) ||
        (c >= 0x045E && c <= 0x0486) ||
        (c >= 0x0490 && c <= 0x04C4) ||
        (c >= 0x04C7 && c <= 0x04C9) ||
        (c >= 0x04CB && c <= 0x04CC) ||
        (c >= 0x04D0 && c <= 0x04EB) ||
        (c >= 0x04EE && c <= 0x04F5) ||
        (c >= 0x04F8 && c <= 0x04F9) ||
        (c >= 0x0591 && c <= 0x05A1) ||
        (c >= 0x05A3 && c <= 0x05C4) ||
        (c >= 0x05D0 && c <= 0x05EA) ||
        (c >= 0x05F0 && c <= 0x05F4) ||
        (c >= 0x060C) ||
        (c >= 0x061B) ||
        (c >= 0x061F) ||
        (c >= 0x0621 && c <= 0x063A) ||
        (c >= 0x0640 && c <= 0x0655) ||
        (c >= 0x0660 && c <= 0x06EE) ||
        (c >= 0x06F0 && c <= 0x06FE) ||
        (c >= 0x0901 && c <= 0x0939) ||
        (c >= 0x093C && c <= 0x094D) ||
        (c >= 0x0950 && c <= 0x0954) ||
        (c >= 0x0958 && c <= 0x0970) ||
        (c >= 0x0E01 && c <= 0x0E3A) ||
        (c >= 0x1E80 && c <= 0x1E85) ||
        (c >= 0x1EA0 && c <= 0x1EF9) ||
        (c >= 0x2000 && c <= 0x202E) ||
        (c >= 0x2030 && c <= 0x2046) ||
        (c == 0x2070) ||
        (c >= 0x2074 && c <= 0x208E) ||
        (c == 0x2091) ||
        (c >= 0x20A0 && c <= 0x20AC) ||
        (c >= 0x2100 && c <= 0x2138) ||
        (c >= 0x2153 && c <= 0x2182) ||
        (c >= 0x2190 && c <= 0x21EA) ||
        (c >= 0x2190 && c <= 0x21EA) ||
        (c >= 0x2000 && c <= 0x22F1) ||
        (c == 0x2302) ||
        (c >= 0x2320 && c <= 0x2321) ||
        (c >= 0x2460 && c <= 0x2469) ||
        (c == 0x2500) ||
        (c == 0x2502) ||
        (c == 0x250C) ||
        (c == 0x2510) ||
        (c == 0x2514) ||
        (c == 0x2518) ||
        (c == 0x251C) ||
        (c == 0x2524) ||
        (c == 0x252C) ||
        (c == 0x2534) ||
        (c == 0x253C) ||
        (c >= 0x2550 && c <= 0x256C) ||
        (c == 0x2580) ||
        (c == 0x2584) ||
        (c == 0x2588) ||
        (c == 0x258C) ||
        (c >= 0x2590 && c <= 0x2593) ||
        (c == 0x25A0) ||
        (c >= 0x25AA && c <= 0x25AC) ||
        (c == 0x25B2) ||
        (c == 0x25BA) ||
        (c == 0x25BC) ||
        (c == 0x25C4) ||
        (c == 0x25C6) ||
        (c >= 0x25CA && c <= 0x25CC) ||
        (c == 0x25CF) ||
        (c >= 0x25D7 && c <= 0x25D9) ||
        (c == 0x25E6) ||
        (c == 0x2605) ||
        (c == 0x260E) ||
        (c == 0x261B) ||
        (c == 0x261E) ||
        (c >= 0x263A && c <= 0x263C) ||
        (c == 0x2640) ||
        (c == 0x2642) ||
        (c == 0x2660) ||
        (c == 0x2663) ||
        (c == 0x2665) ||
        (c == 0x2666) ||
        (c == 0x266A) ||
        (c == 0x266B) ||
        (c >= 0x2701 && c <= 0x2709) ||
        (c >= 0x270C && c <= 0x2727) ||
        (c >= 0x2729 && c <= 0x274B) ||
        (c == 0x274D) ||
        (c >= 0x274F && c <= 0x2752) ||
        (c == 0x2756) ||
        (c >= 0x2758 && c <= 0x275E) ||
        (c >= 0x2761 && c <= 0x2767) ||
        (c >= 0x2776 && c <= 0x2794) ||
        (c >= 0x2798 && c <= 0x27BE) ||
        (c >= 0xF001 && c <= 0xF002) ||
        (c >= 0xF021 && c <= 0xF0FF) ||
        (c >= 0xF601 && c <= 0xF605) ||
        (c >= 0xF610 && c <= 0xF616) ||
        (c >= 0xF800 && c <= 0xF807) ||
        (c >= 0xF80A && c <= 0xF80B) ||
        (c >= 0xF80E && c <= 0xF811) ||
        (c >= 0xF814 && c <= 0xF815) ||
        (c >= 0xF81F && c <= 0xF820) ||
        (c >= 0xF81F && c <= 0xF820) ||
        (c == 0xF833));
  }

  public void initWithParent () {
    controller.parent.registerMethod("mouseEvent", this);
  }
  


  /**
  * adds a character to the immediate right of the insertion point or replaces the selected group of characters. This method is called by <pre>public void MouseEvent</pre> if a unicode character is entered via the keyboard.
  * @param c the character to be added
  */
  
  protected void appendToRightOfCursor(char c) {
    appendToRightOfCursor("" + c);
  }
  
  
  /**
  * adds a string to the immediate right of the insertion point or replaces the selected group of characters.
  * @param s the string to be added
  */
  
  protected void appendToRightOfCursor(String s) {
  
    String t1, t2;
    if (startSelect != -1 && endSelect != -1) {
      int start = Math.min(startSelect, endSelect);
      int end = Math.max(startSelect, endSelect);
      if (start >= end || start < 0 || end > contents.length()) {
        // TODO: Check array bounds!
        // System.out.println("Brendan needs to check array bounds.");
        return;
      }
      
      t1 = contents.substring(0, start);
      t2 = contents.substring(end);
      cursorPos = start;
      startSelect = endSelect = -1;
    } else {
      t1 = contents.substring(0, cursorPos);
      t2 = contents.substring(cursorPos);
    }
    
    contents = t1 + s + t2;
    cursorPos += s.length();
        
    // Adjust the start and end positions of the visible portion of the string
    if (controller.parent.textWidth(contents) < getWidth() - 12) {
      visiblePortionStart = 0;
      visiblePortionEnd = contents.length();
    } else {
      if (cursorPos == contents.length()) {
        visiblePortionEnd = cursorPos;
        adjustVisiblePortionStart();
      } else {
        if (cursorPos >= visiblePortionEnd)
          centerCursor();
        else {
          //visiblePortionEnd = visiblePortionStart;
          adjustVisiblePortionEnd();
        }
        //while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12)
        //  visiblePortionEnd++;
      }
    }

    fireEventNotification(this, "Modified");
  }
  
  
  
  /**
  * deletes either the character directly to the left of the insertion point or the selected group of characters. It automatically handles cases where there is no character to the left of the insertion point (when the insertion point is at the beginning of the string). It is called by <pre>public void keyEvent</pre> when the delete key is pressed.
  */
  
  protected void backspaceChar() {
    if (startSelect != -1 && endSelect != -1) {
      deleteSubstring(startSelect, endSelect);
    } else if (cursorPos > 0){
      deleteSubstring(cursorPos - 1, cursorPos);
    }
  }



  protected void deleteChar() {
    if (startSelect != -1 && endSelect != -1) {
      deleteSubstring(startSelect, endSelect);
    } else if (cursorPos < contents.length()){
      deleteSubstring(cursorPos, cursorPos + 1);
    }
  }


  protected void deleteSubstring(int startString, int endString) {
    int start = Math.min(startString, endString);
    int end = Math.max(startString, endString);
    if (start >= end || start < 0 || end > contents.length()) {
      // TODO: Check array bounds!
      // System.out.println("Brendan needs to check array bounds.");
      return;
    }
    
    contents = contents.substring(0, start) + contents.substring(end);
    cursorPos = start;
    
    if (controller.parent.textWidth(contents) < getWidth() - 12) {
      visiblePortionStart = 0;
      visiblePortionEnd = contents.length();
    } else {
      if (cursorPos == contents.length()) {
        visiblePortionEnd = cursorPos;
        adjustVisiblePortionStart();
      } else {
        if (cursorPos <= visiblePortionStart) {
          centerCursor();
        } else {
          adjustVisiblePortionEnd();
        }
      }
    }
    
    startSelect = endSelect = -1;

    fireEventNotification(this, "Modified");
    //controller.userState.restoreSettingsToApplet(controller.parent);    
  }

  protected void copySubstring(int start, int end) {
    int s = Math.min(start, end);
    int e = Math.max(start, end);
    controller.copy(getValue().substring(s, e));
  }


  // ***** UNTIL GRAPHICS SETTINGS ARE STORED IN A QUEUE, MAKE SURE     *****
  // ***** TO ALWAYS CALL THESE FUNCTIONS INSIDE THE INTERFASCIA DEFAULT *****
  // ***** GRAPHICS STATE. I'M NOT TOUCHING THE GRAPHICS STATE HERE.     *****

  private void updateXPos() {
    cursorXPos = controller.parent.textWidth(contents.substring(visiblePortionStart, cursorPos));
    if (startSelect != -1 && endSelect != -1) {
    
      int tempStart, tempEnd;
      if (endSelect < startSelect) {
        tempStart = endSelect;
        tempEnd = startSelect;
      } else {
        tempStart = startSelect;
        tempEnd = endSelect;
      }
      
      if (tempStart < visiblePortionStart)
        startSelectXPos = 0;
      else
        startSelectXPos = controller.parent.textWidth(contents.substring(visiblePortionStart, tempStart));
      
      if (tempEnd > visiblePortionEnd)
        endSelectXPos = getWidth() - 4;
      else
        endSelectXPos = controller.parent.textWidth(contents.substring(visiblePortionStart, tempEnd));
    }
  }
  
  private void adjustVisiblePortionStart() {
    if (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12) {
      while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12) {
        if (visiblePortionStart == 0)
          break;
        else
          visiblePortionStart--;
      }
    } else {
      while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) > getWidth() - 12) {
        visiblePortionStart++;
      }
    }
  }
  
  private void adjustVisiblePortionEnd() {
    //System.out.println(visiblePortionStart + " to " + visiblePortionEnd + " out of " + contents.length());
    
    // Temporarily correcting for an erroneus precondition. Looking for the real issue
    visiblePortionEnd = Math.min(visiblePortionEnd, contents.length()); 
    
    if (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12) {
      while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12) {
        if (visiblePortionEnd == contents.length())
          break;
        else
          visiblePortionEnd++;
      }
    } else {
      while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) > getWidth() - 12) {
        visiblePortionEnd--;
      }
    }
  }
  
  


  private void centerCursor() {
    visiblePortionStart = visiblePortionEnd = cursorPos;
    
    while (controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12) {
      if (visiblePortionStart != 0)
        visiblePortionStart--;
        
      if (visiblePortionEnd != contents.length())
        visiblePortionEnd++;
        
      if (visiblePortionEnd == contents.length() && visiblePortionStart == 0)
        break;
    }
  }

  /**
  * given the X position of the mouse in relation to the X
  * position of the text field, findClosestGap(int x) will
  * return the index of the closest letter boundary in the 
  * letterWidths array.
  */
  
  private int findClosestGap(int x) {
    float prev = 0, cur;
    if (x < 0) {
      return visiblePortionStart;
    } else if (x > getWidth()) {
      return visiblePortionEnd;
    }
    for (int i = visiblePortionStart; i < visiblePortionEnd; i++) {
      cur = controller.parent.textWidth(contents.substring(visiblePortionStart, i));
      if (cur > x) {
        if (cur - x < x - prev)
          return i;
        else
          return i - 1;
      }
      prev = cur;
    }
    
    // Don't know what else to return
    return contents.length();
  }


  public int getVisiblePortionStart()
  {
    return visiblePortionStart;
  }
  public void setVisiblePortionStart(int VisiblePortionStart)
  {
    visiblePortionStart = VisiblePortionStart;
  }
  
  public int getVisiblePortionEnd()
  {
    return visiblePortionEnd;
  }
  public void setVisiblePortionEnd(int VisiblePortionEnd)
  {
    visiblePortionEnd = VisiblePortionEnd;
  }

  public int getStartSelect()
  {
    return startSelect;
  }
  public void setStartSelect(int StartSelect)
  {
    startSelect = StartSelect;
  }

  public int getEndSelect()
  {
    return endSelect;
  }
  public void setEndSelect(int EndSelect)
  {
    endSelect = EndSelect;
  }

  public int getCursorPosition()
  {
    return cursorPos;
  } 
  public void setCursorPosition(int CursorPos)
  {
    cursorPos = CursorPos;
  }


  /**
  * sets the contents of the text box and displays the
  * specified string in the text box widget.
  * @param val the string to become the text field's contents
  */
  
  public void setValue(String newValue) {
    
    contents = newValue;
    cursorPos = contents.length();
    startSelect = endSelect = -1;
    
    visiblePortionStart = 0;
    visiblePortionEnd = contents.length();
    
    // Adjust the start and end positions of the visible portion of the string
    if (controller != null) {
      if (controller.parent.textWidth(contents) > getWidth() - 12) {
        adjustVisiblePortionEnd();
      }
    }

    fireEventNotification(this, "Set");
  }



  /**
  * returns the string that is displayed in the text area.
  * If the contents have not been initialized, getValue() 
  * returns NULL, if the contents have been initialized but
  * not set, it returns an empty string.
  * @return contents the contents of the text field
  */
  
  public String getValue() {
    return contents;
  }



  /**
  * implemented to conform to Processing's mouse event handler
  * requirements. You shouldn't call this method directly, as
  * Processing will forward mouse events to this object directly.
  * mouseEvent() handles mouse clicks, drags, and releases sent
  * from the parent PApplet. 
  * @param e the MouseEvent to handle
  */

  public void mouseEvent(MouseEvent e) {
    controller.userState.saveSettingsForApplet(controller.parent);
    lookAndFeel.defaultGraphicsState.restoreSettingsToApplet(controller.parent);

    if (e.getAction() == MouseEvent.PRESS) {
      if (isMouseOver(e.getX(), e.getY())) {
        controller.requestFocus(this);
        wasClicked = true;
        endSelect = -1;
        startSelect = cursorPos = findClosestGap(e.getX() - getX());
      } else {
        if (controller.getFocusStatusForComponent(this)) {
          wasClicked = false;
          controller.yieldFocus(this);
          startSelect = endSelect = -1;
        }
      }
    } else if (e.getAction() == MouseEvent.DRAG) {
      /*if (controller.parent.millis() % 500 == 0) {
        System.out.println("MOVE");
        if (e.getX() < getX() && endSelect > 0) {
          // move left
          endSelect = visiblePortionStart = endSelect - 1;
          shrinkRight();
        } else if (e.getX() > getX() + getWidth() && endSelect < contents.length() - 1) {
          // move right
          endSelect = visiblePortionEnd = endSelect + 1;
          shrinkLeft();
        }
      }*/
      endSelect = cursorPos = findClosestGap(e.getX() - getX());
    } else if (e.getAction() == MouseEvent.RELEASE) {
      if (endSelect == startSelect) {
        startSelect = -1;
        endSelect = -1;
      }
    }
    updateXPos();
    controller.userState.restoreSettingsToApplet(controller.parent);
  }


  
  /**
  * receives KeyEvents forwarded to it by the GUIController
  * if the current instance is currently in focus.
  * @param e the KeyEvent to be handled
  */

  public void keyEvent(KeyEvent e) {
    controller.userState.saveSettingsForApplet(controller.parent);
    lookAndFeel.defaultGraphicsState.restoreSettingsToApplet(controller.parent);

    int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    boolean shiftDown = e.isShiftDown();
    if (e.getAction() == KeyEvent.PRESS) {
      if (e.getKey() == java.awt.event.KeyEvent.VK_DOWN) {
        if (shiftDown) {
          if (startSelect == -1)
            startSelect = cursorPos;
          endSelect = cursorPos = visiblePortionEnd = contents.length();
        } else {
          // Shift isn't down
          startSelect = endSelect = -1;
          cursorPos = visiblePortionEnd = contents.length();
        }
        //visiblePortionStart = visiblePortionEnd;
        adjustVisiblePortionStart();
      } 
      else if (e.getKey() == java.awt.event.KeyEvent.VK_UP) {
        if (shiftDown) {
          if (endSelect == -1)
            endSelect = cursorPos;
          startSelect = cursorPos = visiblePortionStart = 0;
        } else {
          // Shift isn't down
          startSelect = endSelect = -1;
          cursorPos = visiblePortionStart = 0;
        }
        //visiblePortionEnd = visiblePortionStart;
        adjustVisiblePortionEnd();
      } 
      else if (e.getKey() == java.awt.event.KeyEvent.VK_LEFT) {
        if (shiftDown) {
          if (cursorPos > 0) {
            if (startSelect != -1 && endSelect != -1) {
              startSelect--;
              cursorPos--;
            } else {
              endSelect = cursorPos;
              cursorPos--;
              startSelect = cursorPos;
            }
          }
        } else {
          if (startSelect != -1 && endSelect != -1) {
            cursorPos = Math.min(startSelect, endSelect);
            startSelect = endSelect = -1;
          } else if (cursorPos > 0) {
            cursorPos--;
          }
        }
        centerCursor();
      } 
      else if (e.getKey() == java.awt.event.KeyEvent.VK_RIGHT) {
        if (shiftDown) {
          if (cursorPos < contents.length()) {
            if (startSelect != -1 && endSelect != -1) {
              endSelect++;
              cursorPos++;
            } else {
              startSelect = cursorPos;
              cursorPos++;
              endSelect = cursorPos;
            }
          }
        } else {
          if (startSelect != -1 && endSelect != -1) {
            cursorPos = Math.max(startSelect, endSelect);
            startSelect = endSelect = -1;
          } else if (cursorPos < contents.length()) {
            cursorPos++;
          }
        }
        centerCursor();
      } 
      else if (e.getKey() == java.awt.event.KeyEvent.VK_DELETE) {
        deleteChar();
      }
      else if (e.getKey() == java.awt.event.KeyEvent.VK_ENTER) {
        fireEventNotification(this, "Completed");
      }
      else{
        if ((e.getModifiers() & shortcutMask) == shortcutMask) {
          switch (e.getKey()) {
            case java.awt.event.KeyEvent.VK_C:
              if (startSelect != -1 && endSelect != -1) {
                copySubstring(startSelect, endSelect);
              }
              break;
            case java.awt.event.KeyEvent.VK_V:
              appendToRightOfCursor(controller.paste());
              break;
            case java.awt.event.KeyEvent.VK_X:
              if (startSelect != -1 && endSelect != -1) {
                copySubstring(startSelect, endSelect);
                deleteSubstring(startSelect, endSelect);
              }
              break;
            case java.awt.event.KeyEvent.VK_A:
              startSelect = 0;
              endSelect = contents.length();
              break;
          }
        } 
      }
    } 
    else if (e.getAction() == KeyEvent.TYPE) {
      if ((e.getModifiers() & shortcutMask) == shortcutMask) {
      }
      else if (e.getKey() == '\b') {
        backspaceChar();
      } 
      else if (e.getKey() != java.awt.event.KeyEvent.CHAR_UNDEFINED) {
        if(validUnicode(e.getKey()))
          appendToRightOfCursor(e.getKey());
      }
    }
    updateXPos();

    controller.userState.restoreSettingsToApplet(controller.parent);
  }
  
  
  
  /**
  * draws the text field, contents, selection, and cursor
  * to the screen.
  */
  
  public void show () {
    boolean hasFocus = controller.getFocusStatusForComponent(this);
    
    /*if (wasClicked) {
      currentColor = lookAndFeel.activeColor;
    } else if (isMouseOver (controller.parent.mouseX, controller.parent.mouseY) || hasFocus) {
      currentColor = lookAndFeel.highlightColor;
    } else {
      currentColor = lookAndFeel.baseColor;
    }*/

    // Draw the surrounding box
    controller.parent.stroke(lookAndFeel.highlightColor);
    controller.parent.fill(lookAndFeel.borderColor);
    controller.parent.rect(getX(), getY(), getWidth(), getHeight());
    controller.parent.noStroke();

    // Compute the left offset for the start of text
    // ***** MOVE THIS TO SOMEWHERE THAT DOESN'T GET CALLED 50 MILLION TIMES PER SECOND ******
    float offset;
    if (cursorPos == contents.length() && controller.parent.textWidth(contents) > getWidth() - 8)
      offset = (getWidth() - 4) - controller.parent.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd));
    else
      offset = 4;

    // Draw the selection rectangle
    if (hasFocus && startSelect != -1 && endSelect != -1) {
      controller.parent.fill(lookAndFeel.selectionColor);
      controller.parent.rect(getX() + startSelectXPos + offset, getY() + 3, endSelectXPos - startSelectXPos + 1, 15);
    }

    // Draw the string
    controller.parent.fill (lookAndFeel.textColor);
    controller.parent.text (contents.substring(visiblePortionStart, visiblePortionEnd), getX() + offset, getY() + 5, getWidth() - 8, getHeight() - 6);

    // Draw the insertion point (it blinks!)
    if (hasFocus && (startSelect == -1 || endSelect == -1) && ((controller.parent.millis() % 1000) > 500)) {
      controller.parent.stroke(lookAndFeel.darkGrayColor);
      controller.parent.line(getX() + (int) cursorXPos + offset, getY() + 3, getX() + (int) cursorXPos + offset, getY() + 18);
    }
  }

  public void actionPerformed(GUIEvent e) {
    super.actionPerformed(e);
    if (e.getSource() == this) {
      if (e.getMessage().equals("Received Focus")) {
        if (contents != "") {
          startSelect = 0;
          endSelect = contents.length();
        }
      } else if (e.getMessage().equals("Lost Focus")) {
        if (contents != "") {
          startSelect = endSelect = -1;
        }
      }
    }
  }
}
class LangImage{
  PImage image;
  PVector pos;
  String name;
  boolean mdown=false;
  XML xml;
  BlackboxSticksExporter bbse;
  LangImage(File f,PVector pos,BlackboxSticksExporter s){
    this.bbse=s;
    image = loadImage(f.getAbsolutePath());
    name = f.getName().substring(0,f.getName().length()-4);
    this.pos = pos;
    xml = loadXML(sketchPath()+"/assets/data.xml");
  }
  public boolean update(){
    boolean ret = false;
    if(mouseY>pos.y-15&&mouseY<pos.y+15&&mouseX>pos.x-25&&mouseX<pos.x+25&&mousePressed&&mdown!=true){
      mdown=true;
      xml = loadXML(sketchPath()+"/assets/data.xml");
      xml.getChildren("data")[4].setContent(name);
      saveXML(xml,sketchPath()+"/assets/data.xml");
      setup();
      ret = true;
    }
    
      
    if(!mousePressed){mdown=false;}
    return ret;
  }
  public PImage getImage(){
    return this.image;
  }
  
}
class Language{
  String name;
  XML[] content;
  String[]contentstring;
  PImage langImage;
  Language(String name){
    this.name=name;
    langImage = loadImage(sketchPath()+"/assets/lang/images/"+name+".png");
    if(name.equals("ru")||name.equals("ua")){
      contentstring = loadStrings(sketchPath()+"/assets/lang/lang/"+name+".xml");
    }
    loadLang();
  }
  public void loadLang(){
    content = loadXML(sketchPath()+"/assets/lang/lang/"+name+".xml").getChildren("text");
  }
  public String getTranslation(String id){
    String ret = "NO TRANSLATION!";
    for(int i = 0; i< content.length; i++){
      if(content[i].getString("id").equals(id)){
        if(name.equals("ru")||name.equals("ua")){
            ret= contentstring[i+1].substring(contentstring[i+1].length()-7-content[i].getContent().length(),contentstring[i+1].length()-7);

          
        }else{
          ret = content[i].getContent()+"";
        }
      }
    }
    return ret;
  }
  public PImage getImage(){
    return langImage;
  }
}
class Preview{
  int posy;
  float offset=20;
  float scrollPos=0.0f;
  int scrollControlHeight=50;
  int windowHeight=0;
  int activeRenderers=0;
  RenderManager rm;
  Renderer[] renderers;
  Preview(int posy, RenderManager manager){
    this.posy=posy;
    this.rm = manager;
    renderers = rm.getRenderers();
    windowHeight=max((renderers.length+1)*50-(height-posy+10),0);
  }
  
  public void show(){
    offset=-map(scrollPos,0,1,0,windowHeight)+25;
    fill(25);
    stroke(0);
    rect(0,posy,width,height-20-posy);
    fill(38);
    rect(width-15,posy,20,height-15);
    int posindex=0;
    for(int i = 0; i< renderers.length;i++){
      if(renderers[i].currentState.equals(lang.getTranslation("rendererStateDone"))){
        activeRenderers++;
      }
      if(renderers[i].running){
        fill(150);
        textAlign(LEFT,TOP);
        text(renderers[i].file.getName().substring(0,renderers[i].file.getName().length()-4),0,posy+posindex*50+offset);
        textAlign(RIGHT,TOP);
        text(renderers[i].currentState,width-100,posy+posindex*50+offset);
        textAlign(CENTER,TOP);
        String sec1;
        String sec2="00";
        String sec21="0:";
        if(renderers[i].getRuntime()%60<10){sec1="0"+renderers[i].getRuntime()%60;}else{sec1=renderers[i].getRuntime()%60+"";}
        if(renderers[i].getETA()!=MAX_INT){
        if(renderers[i].getETA()%60<10){sec2="0"+renderers[i].getETA()%60;}else{sec2=renderers[i].getETA()%60+"";}
        sec21=renderers[i].getETA()/60+":";
        }
        text(renderers[i].getRuntime()/60+":"+sec1+"    |    "+sec21+sec2,(width-100)/2,posy+posindex*50+offset);
        
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
        imageMode(CORNER);
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
    
    strokeWeight(3);
    stroke(0);
    fill(25);
    rect(0,posy+1,width-15,20);
    fill(255,0,0);
    rect(0,posy+1,map(constrain(activeRenderers,0,renderers.length),0,renderers.length,0,width-15),20);
    strokeWeight(1);
    fill(150);
    stroke(0);
    textAlign(CENTER,TOP);
    text(activeRenderers+" / "+renderers.length,(width-15)/2,posy+3);
    
    fill(60);
    rect(width-15,map(scrollPos,0,1,posy,height-20-scrollControlHeight),15,scrollControlHeight);
    activeRenderers=0;
  }
  public void mouseDown(){
  if(mouseX>width-15&&mouseY>map(scrollPos,0,1,posy,height-20-scrollControlHeight)&&mouseY<map(scrollPos,0,1,posy,height-20-scrollControlHeight)+scrollControlHeight);{
    scrollPos=constrain(map(mouseY-scrollControlHeight/2,posy,height-20-scrollControlHeight,0,1),0,1);
  }
  }

}
class RamTable{
  private String[]content;
  RamTable(String file){
    content = loadStrings(file);
  }
  public RamTableRow getRow(int index){
    return new RamTableRow(content[index]);
  }
  public int getRowCount(){return content.length;}
}
class RamTableRow{
  private String[]content;
  RamTableRow(String row){
    content = row.split(",");
  }
  public String getString(int index){return content[index];}
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
          renderers[i].currentState=lang.getTranslation("rendererStateDone");
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
          renderers[i].currentState=lang.getTranslation("rendererStateDone");
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
      if(f[i].getName().contains(".BFL")||f[i].getName().contains(".bbl")){
        filenum++;
      }
    }
    files= new File[filenum];
    int index=0;
    for(int i = 0; i< f.length;i++){
      if(f[i].getName().contains(".BFL")||f[i].getName().contains(".bbl")){
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
  File[] logs;
  boolean running = false,stop=false;
  int numFrames, currentFrame=0;
  String[]settings;
  String currentState="";
  int number;
  int tailLength,vidWidth,borderThickness,bgOpacity,sticksMode,sticksModeVertPos;
  float fps;
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
    currentState=lang.getTranslation("rendererStateConverting");
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

    logs = getLogs(file);
    }catch(Exception e) {
    e.printStackTrace();
    }
  }
  public void textStroke(String s,float x,float y,float stroke, int res,PGraphics g){
  PVector pos = new PVector(0,stroke);
  float da = TWO_PI/res;
  for(int i = 0; i< res; i++){
    g.text(s,x+pos.x,y+pos.y);
    pos.rotate(da);
  }
}


  public void renderLog(int sublog){
    if(stop){return;}
    currentState=lang.getTranslation("rendererStateRendering");
    int w = vidWidth;
    RamTable table = new RamTable(file.getAbsolutePath());
    if(table.getRowCount()<=2){return;}
    PGraphics alphaG=createGraphics(vidWidth,vidWidth/2);
    PGraphics border=createGraphics(vidWidth,vidWidth/2);
    int startindex=1;
    float lengthus=(PApplet.parseFloat(table.getRow(table.getRowCount()-1).getString(1).trim())-PApplet.parseFloat(table.getRow(startindex).getString(1).trim()));
    int lengthlist = table.getRowCount()-startindex;
    numFrames=(int)((lengthus/1000000.0f)*fps);
    float space=PApplet.parseFloat(lengthlist)/((lengthus/1000000.0f)*fps);
  int where=0;
  for (float i = startindex; i<table.getRowCount(); i+=space) {
    if(stop){return;}
    RamTableRow row = table.getRow(PApplet.parseInt(i));
    currentFrame++;
    if(borderThickness>0){
    border.beginDraw();
    border.clear();
    border.fill(0,0,0,255);
    border.stroke(0, 0, 0, 255);
    border.strokeWeight(w/400);
    //vert
    border.rect(w/2-w/30-w/6-w/400-borderThickness, ((w/3)/7.3f)/2-borderThickness, w/200+borderThickness*2, w/3+borderThickness*2 );
    border.rect(w/2+w/30+w/6-w/400-borderThickness, ((w/3)/7.3f)/2-borderThickness, w/200+borderThickness*2, w/3+borderThickness*2 );
    //hori
    border.rect(w/2-w/30-w/3-borderThickness, w/6-w/400+((w/3)/7.3f)/2-borderThickness, w/3+borderThickness*2, w/200+borderThickness*2);
    border.rect(w/2+w/30-borderThickness, w/6-w/400+((w/3)/7.3f)/2-borderThickness, w/3+borderThickness*2, w/200+borderThickness*2);
    
    //text
    border.textSize(w/25);
    if(sticksMode == 2){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke(PApplet.parseInt(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(-PApplet.parseInt(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(PApplet.parseInt(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
    }else if(sticksMode ==3){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke(PApplet.parseInt(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(PApplet.parseInt(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(-PApplet.parseInt(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
    
    }if(sticksMode == 1){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke(PApplet.parseInt(row.getString(14).trim())+"", w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(-PApplet.parseInt(row.getString(15).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(PApplet.parseInt(row.getString(13).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
    }if(sticksMode == 4){
      //throttle
      border.textAlign(LEFT, CENTER);
      textStroke((int)map(constrain(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500), -500, 500, 2000, 1000)+"", w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //pitch
      border.textAlign(RIGHT, CENTER);
      textStroke(PApplet.parseInt(row.getString(14).trim())+"", w-w/75, w/6-w/200+((w/3)/7.3f)/2,borderThickness,20,border);
      //yaw
      border.textAlign(CENTER, BOTTOM);
      textStroke(PApplet.parseInt(row.getString(13).trim())+"", w/2-w/30-w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
      //roll
      border.textAlign(CENTER, BOTTOM);
      textStroke(-PApplet.parseInt(row.getString(15).trim())+"", w/2+w/30+w/6-w/400, (w/2.2f)-w/75,borderThickness,20,border);
    }
    
    

    border.noStroke();
    float taillength=(fps/30)*tailLength;
    float prevx=0;
    float prevy=0;
    float prevx1=0;
    float prevy1=0;
    for (float j = 0; j< space*taillength; j++) {
      RamTableRow trailrow = table.getRow(constrain(PApplet.parseInt(i-j), 0, table.getRowCount()-1));
      border.fill(0,0,0,255);
      float r = map(j, 0, space*taillength, ((w/3)/7.3f)/1.5f, ((w/3)/7.3f)/10)+borderThickness*2;
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
      if (x!=prevx||y!=prevy) {
        border.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1||y1!=prevy1) {
        border.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
      trailrow=null;
    }
    border.fill(0,0,0,255);
    
    border.textAlign(CENTER,CENTER);
    if(sticksMode==2||sticksMode==4){
    textStroke("Mode"+sticksMode,w/2-w/30-w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3f)/2,(((w/3)/7.3f)/2)+w/3),borderThickness,20,border);
    }else{
    textStroke("Mode"+sticksMode,w/2+w/30+w/6-w/400,map(sticksModeVertPos,0,100,((w/3)/7.3f)/2,(((w/3)/7.3f)/2)+w/3),borderThickness,20,border);
    }
    border.fill(0,0,0, 255);
    if (sticksMode==2) {
      border.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
      border.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
    }else if(sticksMode==3){
      border.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
      border.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
    
      }
    else if(sticksMode==1){
       border.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
      border.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
    }if (sticksMode==4) {
      border.ellipse(map(PApplet.parseInt(row.getString(13).trim()), -500, 500, w/2-w/30-w/3, w/2-w/30-w/3+w/3), map(-(int)map(PApplet.parseInt(row.getString(16).trim()), 1000, 2000, -500, 500), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
      border.ellipse(map(-PApplet.parseInt(row.getString(15).trim()), -500, 500, w/2+w/30, w/2+w/30+w/3), map(-PApplet.parseInt(row.getString(14).trim()), -500, 500, 0, w/3)+((w/3)/7.3f)/2, (w/3)/7.3f+borderThickness*2, (w/3)/7.3f+borderThickness*2);
    }
    border.endDraw();
    
    
    }
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
      RamTableRow trailrow = table.getRow(constrain(PApplet.parseInt(i-j), 0, table.getRowCount()-1));
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
      if (x!=prevx||y!=prevy) {
        alphaG.ellipse(x, y, r, r);
        prevx=x;
        prevy=y;
      }
      if (x1!=prevx1||y1!=prevy1) {
        alphaG.ellipse(x1, y1, r, r);
        prevx1=x1;
        prevy1 =y1;
      }
      trailrow=null;
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
    
    PGraphics out = createGraphics(alphaG.width, alphaG.height, JAVA2D);
    out.beginDraw();
    out.clear();
    if (bgOpacity>0) {
      out.background(bgColor, bgOpacity);
    }
    float scl= 0.01f;
    if (borderThickness>0) {
      
      out.image(border, vidWidth*scl*2,vidWidth*scl,vidWidth - vidWidth*scl*4,vidWidth/2 - vidWidth*scl*2);
      out.filter(BLUR,borderThickness/3);
    }
    out.image(alphaG,vidWidth*scl*2,vidWidth*scl,vidWidth - vidWidth*scl*4,vidWidth/2 - vidWidth*scl*2);

    out.endDraw();
    out.save("temp/"+number+"/"+sublog+"/Images/line_"+("0000000000"+where).substring((where+"").length())+".png"); 

    prevImage = out;
    where++;
    row = null;
  }
  table = null;
  }
  public void compileLog(int sublog){
    if(stop){return;}
    currentState=lang.getTranslation("rendererStateCompiling");
    ProcessBuilder processBuilder = new ProcessBuilder(sketchPath()+"/assets/ffmpeg.exe", "-r", fps+"", "-i", sketchPath()+"/temp/"+number+"/"+sublog+"/Images/line_%010d.png", "-vcodec", "prores_ks","-pix_fmt", "yuva444p10le", "-profile:v", "4444", "-q:v", "20", "-r", fps+"", "-y", outputPath.getAbsolutePath()+"/"+file.getName().substring(0,file.getName().length()-4)+".mov");
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
    fps=Float.parseFloat(settings[0]);
    tailLength=Integer.parseInt(settings[1]);
    vidWidth=Integer.parseInt(settings[2]);
    borderThickness=(int)map(Integer.parseInt(settings[3]),0,100,0,(vidWidth/75.0f));
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
        
        for(int i = 0; i< logs.length; i++){
          starttime=millis();
          file = logs[i];
          renderLog(i);
          starttime=millis();
          compileLog(i);
        }
    

    deleteDir(new File(sketchPath()+"/temp/"+number+"/"));
    running=false;
    currentState="Done";
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
  public File[] getLogs(File f){
    File[] files = new File(f.getParent()).listFiles();
    LinkedList<File> files2 = new LinkedList<File>();
    for(int i = 0; i< files.length;i++){
      if(files[i].getAbsolutePath().contains(".csv")&&files[i].getAbsolutePath().contains(f.getName().substring(0,f.getName().length()-4))){

        File del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".event");
        del.delete();
        del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".gps.csv");
        del.delete();
        del = new File(files[i].getAbsolutePath().substring(0,files[i].getAbsolutePath().length()-4)+".gps.gpx");
        del.delete();
        files[i].renameTo(new File(sketchPath()+"/temp/"+number+"/csv/"+files[i].getName()));
        files[i] = new File(sketchPath()+"/temp/"+number+"/csv/"+files[i].getName());
        files2.add(files[i]);
      }
    }
    files = new File[files2.size()];
    for(int i = 0; i< files.length;i++){
      files[i] = files2.get(i);
    }
    return files;
  }
}

class Selection{
  Preview p;
  boolean active = false;
  boolean rendering=false;
  boolean tempClear=false;
  GUIController c1;
  IFButton settings,btnInputFolder,btnOutputFolder,btnStart,addRenderer,btnCredits;
  IFCheckBox checkSound, checkShutdown,checkLogger,checkAutoFolder;
  BlackboxSticksExporter bbse;
  PApplet applet;
  Settings set;
  Credits credits;
  SoundFile sound;

  String console="";
  boolean readyToRender =false;
  
PrintStream stdout=System.out;
PrintStream stderr=System.err;
  Selection(BlackboxSticksExporter applet){
    active = true;
    this.applet = applet;
    createSelectionGui();
   sound = new SoundFile(applet,"assets/done.wav");
  }
  public void show(){
    
    if(set!=null){set.show();}
    if(credits!=null){credits.show();}
    if(rendering){
      p.show();
    }
    
    textAlign(CENTER,BOTTOM);
    fill(100);
    stroke(0);
    rect(0,height-20,width,20);
    fill(0);
    text(console,width/2,height-2);
    if(c1 != null){
    c1.show();
    }
  }
  
  public void actionPerformed(GUIEvent e){
    if(this.active){
      if(e.getSource()==btnCredits){
        if(rm!=null&&rm.rendering){
          console=lang.getTranslation("consoleErrorOpenCreditsWhileRendering");
        }else{
        removeSelectionGui();
        credits = new Credits(this,applet);
        }
      }
      if(e.getSource()==checkAutoFolder){
        if(checkAutoFolder.isSelected()){
        if(inputPath!=null){
          outputPath=new File(inputPath.getAbsolutePath()+"/Output/");
          createImage(1, 1, RGB).save(outputPath.getAbsolutePath()+"/temp.png");
          new File(outputPath.getAbsolutePath()+"/temp.png").delete();
        }
      }else{
        outputPath=new File(data.getChildren("data")[1].getContent());
      }
      }
      if(e.getSource()==checkLogger){
        if(checkLogger.isSelected()){
          try{
            saveStrings(sketchPath()+"/assets/logs/log.txt",new String[]{""});
            saveStrings(sketchPath()+"/assets/logs/errlog.txt",new String[]{""});
            PrintStream err = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/errlog.txt",true),true);
            System.setErr(err);
            PrintStream out = new PrintStream(new FileOutputStream(sketchPath()+"/assets/logs/log.txt",true),true);
            System.setOut(out);
          }catch(Exception ex){
            ex.printStackTrace();
          }
        }else{
          System.setOut(stdout);
          System.setErr(stderr);
          
        }
      }
      if(e.getSource()==settings){
        if(rm!=null&&rm.rendering){
          console=lang.getTranslation("consoleErrorOpenSettingsWhileRendering");
        }else{
        removeSelectionGui();
        set = new Settings(applet,this,bbse);
        }
      }
      if(e.getSource() == btnInputFolder){
        if(inputPath!=null){
          selectFolder(lang.getTranslation("selectInputFolder"),"InputSelected",new File(inputPath.getAbsolutePath()));
        }else{
          selectFolder(lang.getTranslation("selectInputFolder"),"InputSelected");
        }
      }
      if(e.getSource() == btnOutputFolder){
        if(outputPath!=null){
          selectFolder(lang.getTranslation("selectOutputFolder"),"OutputSelected",new File(outputPath.getAbsolutePath()));
        }else{
          selectFolder(lang.getTranslation("selectOutputFolder"),"OutputSelected");
        }
      }if(e.getSource()==addRenderer){
        if(rm!=null){
          rm.simultRenderNum++;
          XML xml = loadXML("assets/settings.xml");
          XML[] children = xml.getChildren("setting");
          children[9].setContent(rm.simultRenderNum+"");
          saveXML(xml,"assets/settings.xml");
        }
      }
      if(e.getSource()==btnStart){
        readyToRender = true;
        if(inputPath==null&&inputFiles.size()==0){
          console=lang.getTranslation("consoleErrorSelectInputFolder");
          readyToRender = false;
        }
        if(outputPath==null){
          console=lang.getTranslation("consoleErrorSelectOutputFolder");
          readyToRender = false;
        }
        if(outputPath==null&&inputPath==null&&inputFiles.size()==0){
          console=lang.getTranslation("consoleErrorSelectInputAndOutputFolder");
          readyToRender = false;
        }
        if(outputPath!=null&&inputPath!=null){
          if(outputPath.getAbsolutePath().equals(inputPath.getAbsolutePath())){
            console=lang.getTranslation("consoleErrorInputOutputEqual");
            readyToRender = false;
          }
        }
        if(inputPath!=null){
          if(getNumFiles(inputPath,"BFL")<=0){
            console=lang.getTranslation("consoleErrorEmptyInput");
            readyToRender=false;
          }
        }
        if(!tempClear){
          console=lang.getTranslation("consoleErrorTempNotEmpty");
          readyToRender=false;
        }
        if(readyToRender==true&&rm==null){
          
          console=lang.getTranslation("consoleInfoStartRender");
          if(inputPath!=null){
            rm=new RenderManager(inputPath.listFiles(),getCurrentSettings(),this);
          }else{
            File[] files = new File[inputFiles.size()];
            for(int i = 0; i< inputFiles.size();i++){
              files[i] = inputFiles.get(i);
            }
            
            rm = new RenderManager(files,getCurrentSettings(),this);
          }
          rendering = true;
          p = new Preview(200,rm);
        }
      }
    }
    if(set!=null){
      set.actionPerformed(e);
    }if(credits!=null){
      credits.actionPerformed(e);
    }
  }
  
  public boolean active(){
    return this.active;
  }
  public void createSelectionGui(){
    c1 = new GUIController(applet);
    
    settings = new IFButton(lang.getTranslation("btnChangeSettings"),50,30,150,20);
    settings.addActionListener(applet);
    btnInputFolder = new IFButton(lang.getTranslation("btnSelectInputFolder"),300,30,200,20);
    btnInputFolder.addActionListener(applet);
    btnOutputFolder = new IFButton(lang.getTranslation("btnSelectOutputFolder"),300,90,200,20);
    btnOutputFolder.addActionListener(applet);
    btnStart = new IFButton(lang.getTranslation("btnStart"),300,150,200,20);
    btnStart.addActionListener(applet);
    btnCredits = new IFButton(lang.getTranslation("btnCredits"),50,90,150,20);
    btnCredits.addActionListener(applet);
    checkSound = new IFCheckBox(lang.getTranslation("checkBoxFinishSound"),750,30);
    c1.add(checkSound);
    checkShutdown = new IFCheckBox(lang.getTranslation("checkBoxShutdown"),750,70);
    checkLogger = new IFCheckBox(lang.getTranslation("checkBoxLogger"),750,110);
    checkLogger.addActionListener(applet);
    checkAutoFolder = new IFCheckBox(lang.getTranslation("checkBoxAutoFolder"),750,150);
    checkAutoFolder.addActionListener(applet);
    addRenderer = new IFButton(lang.getTranslation("btnAddRenderer"),50,150,150,20);
    addRenderer.addActionListener(applet);
    c1.add(btnCredits);
    c1.add(addRenderer);
    c1.add(checkAutoFolder);
    c1.add(checkShutdown);
    c1.add(checkLogger);
    c1.add(btnInputFolder);
    c1.add(btnStart);
    c1.add(btnOutputFolder);
    c1.add(settings);
    active = true;
  }
  public void removeSelectionGui(){
    c1.remove(settings);
    c1.remove(btnCredits);
    c1.remove(btnInputFolder);
    c1.remove(btnOutputFolder);
    settings.addActionListener(null);
    btnInputFolder.addActionListener(null);
    btnOutputFolder.addActionListener(null);
    btnCredits.addActionListener(null);
    c1.setVisible(false);
    c1=null;
    settings = null;
    btnCredits=null;
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
    rm=null;
    setup();
  }
  public void mousePress(){
    if(set!=null){
      set.mousePress();
      
    }
    if(credits!=null){
      credits.mousePress();
    }
  }
  public void mouseRel(){
    if(set!=null){
      set.mouseRel();
      
    }
  }
  public void savePreset(File f){
    if(set!=null){set.savePreset(f);}
  }
  public void loadPreset(File f){
    if(set!=null){set.loadPreset(f);}
  }
}
class Settings{
  boolean active = false;
  GUIController c;
  PApplet applet;
  XML xml;
  String explanations[]={
    lang.getTranslation("descFPS"),
lang.getTranslation("descTraillength"),
lang.getTranslation("descWidth"),
lang.getTranslation("descBorderThickness"),
lang.getTranslation("descBackgroundColor"),
lang.getTranslation("descBackgroundOpacity"),
lang.getTranslation("descSticksMode"),
lang.getTranslation("descSticksModeVertPos"),
lang.getTranslation("descStickColor"),
lang.getTranslation("descSimultaneousRenderers")
};
  IFLabel labels[];
  String labelstext[];
  IFLabel explain;
  IFTextField fields[];
  IFLookAndFeel look;
  IFButton back;
  IFButton save;
  IFButton defaults;
  IFButton savePreset;
  IFButton loadPreset;
  Selection selection;
  ColorWheel cw;
  LangImage[] langImage;
  BlackboxSticksExporter bbse;
  String activeLang;
  PImage icocw;
  Settings(PApplet applet,Selection selection,BlackboxSticksExporter bbse){
    this.bbse = bbse;
    this.applet = applet;
    this.look = look;
    this.selection = selection;
    icocw = loadImage(sketchPath()+"/assets/cw.png");
    c = new GUIController(applet);
    initXML();
    createSettingsGui();
    File[] languages = new File(sketchPath()+"/assets/lang/images").listFiles();
    langImage = new LangImage[languages.length];
    for(int i = 0; i<langImage.length; i++){
      langImage[i] = new LangImage(languages[i],new PVector(width/2+80*i-40*(languages.length-1),height-50),bbse);
    }
    activeLang = loadXML(sketchPath()+"/assets/data.xml").getChildren("data")[4].getContent();
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

        if(fields[i].getLabel().contains("col")){
          imageMode(CORNER);
          fill(80);
          stroke(50);
          rect(568,78+30*i,24,24);
          image(icocw,570,80+30*i);
        }
      }
      for(int i = 0; i<fields.length;i++){

        if(labels[i].isMouseOver(mouseX,mouseY)&&cw==null){
          fill(255);
          rectMode(CORNER);
          stroke(0);
          String temp = trimString(explanations[i],40);
          rect(5+mouseX,mouseY-5,textWidth(temp)+10,textHeight(temp)+23);
          explain.setLabel(temp);
          explain.setPosition(10+mouseX,mouseY);
        }
      }
      for(int i = 0; i<langImage.length;i++){
      if(langImage[i].name.equals(activeLang)){
        rectMode(CENTER);
        noFill();
        stroke(255,0,0);
        strokeWeight(5);
        rect(langImage[i].pos.x,langImage[i].pos.y,50,30);
        strokeWeight(1);
        rectMode(CORNER);}
    }
    for(int i = 0; i<langImage.length;i++){
      boolean temp = langImage[i].update();
      if(temp){activeLang=langImage[i].name;}
      imageMode(CENTER);
    
      image(langImage[i].getImage(),langImage[i].pos.x,langImage[i].pos.y,50,30);
    }
    }
    if(cw!=null){
      stroke(0);
      fill(100);
      rect(cw.posx-20,cw.posy-20,cw.w+40,cw.w+40);
      cw.show();
      strokeWeight(1);
      
    }
    if(c!=null){c.show();}
  }
  public void actionPerformed(GUIEvent e){
    if(this.active){
      if(e.getSource()==savePreset){
        selectOutput(lang.getTranslation("btnSavePreset"),"savePreset",new File(sketchPath()+"/presets/ "));
      }
      if(e.getSource()==loadPreset){
        selectInput(lang.getTranslation("btnLoadPreset"),"loadPreset",new File(sketchPath()+"/presets/ "));
      }
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
        float floatcontent = 0.0f;
        if(args.length==3){
          numcontent = constrain(Integer.parseInt(content),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }else if(args.length==2){
            numcontent = max(Integer.parseInt(content),Integer.parseInt(args[1]));

            floatcontent = max(Float.parseFloat(content),Float.parseFloat(args[1]));
          
        }
        temp.setValue(""+abs(numcontent));
        if(floatcontent!=0){
          temp.setValue(""+abs(floatcontent));
        }
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
    back = new IFButton(lang.getTranslation("btnBack"),50,30,100,20);
    back.addActionListener(applet);
    if(lang.name.equals("ua")){
      savePreset = new IFButton(lang.getTranslation("btnSavePreset"),50,400,150,40);
      savePreset.addActionListener(applet);
    
      loadPreset = new IFButton(lang.getTranslation("btnLoadPreset"),width-200,400,150,40);
      loadPreset.addActionListener(applet);
    }else{   
      savePreset = new IFButton(lang.getTranslation("btnSavePreset"),50,400,150,20);
      savePreset.addActionListener(applet);
    
      loadPreset = new IFButton(lang.getTranslation("btnLoadPreset"),width-200,400,150,20);
      loadPreset.addActionListener(applet);
    }   
    save = new IFButton(lang.getTranslation("btnSave"),width-150,30,100,20);
    save.addActionListener(applet);
    explain = new IFLabel("",-100,-100);
    defaults = new IFButton(lang.getTranslation("btnLoadDefaults"),width/2-75,30,150,20);
    defaults.addActionListener(applet);
    c.add(defaults);
    c.add(back);
    c.add(save);
    c.add(loadPreset);
    c.add(savePreset);
    
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
    s.console = lang.getTranslation("consoleInfoSettingsSaved");
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
    if(mouseButton == 37){
      for(int i = 0; i< fields.length; i++){
        if(fields[i].getLabel().contains("col")&&mouseX>570&&mouseX<590&&mouseY>80+30*i&&mouseY<100+30*i){
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
  
  public String trimString(String s, int l){
    String ret="";
    int index=0;
    for(int i = 0; i< s.length();i++){
      ret+=s.charAt(i);
      if(index>l&&s.charAt(i)==' '){
        ret+='\n';
        index=0;
      }
      index++;
    }
    return ret;
  }
  public void savePreset(File f){
    writeXML();
    if(f.getAbsolutePath().contains(".bbconf")){
      
    saveXML(xml,f.getAbsolutePath());
    }else{
    saveXML(xml,f.getAbsolutePath()+".bbconf");
    }
  }
  public void loadPreset(File f){
    if(f.getAbsolutePath().contains(".bbconf")){
      saveXML(loadXML(f.getAbsolutePath()),sketchPath()+"/assets/settings.xml");
      xml=loadXML("assets/settings.xml");
    XML[] children = xml.getChildren("setting");
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
