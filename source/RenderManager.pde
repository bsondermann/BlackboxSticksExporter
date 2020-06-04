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
  @Override void run(){
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
  void initRenderers(){
    renderers = new Renderer[files.length];
    for(int i = 0; i<renderers.length; i++){
      renderers[i] = new Renderer(files[i],renderSettings,i);
    }
  }
  void filterFiles(File[]f){
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
  Renderer[] getRenderers(){
    return renderers;
  }
  void stopRender(){
    if(rendering){
      for(int i = 0; i< renderers.length;i++){
        renderers[i].stopRender();
      }
      rendering=false;
    }
  }
}
