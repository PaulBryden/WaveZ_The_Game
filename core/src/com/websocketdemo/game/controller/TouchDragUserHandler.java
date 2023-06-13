package com.websocketdemo.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.websocketdemo.game.model.UserEntity;

public class TouchDragUserHandler extends DragListener {
    UserEntity m_Entity;
    public TouchDragUserHandler(UserEntity e) {
        super();
        m_Entity=e;
        setTapSquareSize(30);
    }

    @Override
    public void touchDragged (InputEvent event, float x, float y, int pointer) {
        super.touchDragged(event,x,y,pointer);
        if(super.isDragging()){
            Vector2 touchDrag=new Vector2(super.getDeltaX(),super.getDeltaY());
            if((touchDrag.x+touchDrag.y)>0){
                float adjustParam=touchDrag.len();
                m_Entity.m_Data.pointerAngleRads+=adjustParam*0.03;
            }else{
                float adjustParam=touchDrag.len();
                m_Entity.m_Data.pointerAngleRads-=adjustParam*0.03;

            }
        }
    }

}
