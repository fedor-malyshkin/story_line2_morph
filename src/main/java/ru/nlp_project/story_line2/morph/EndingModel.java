package ru.nlp_project.story_line2.morph;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

class EndingModel implements Serializable {
  public class Ending implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8494281576969553094L;
    Grammemes grammemes;
    String value;

    public Ending(String value, Grammemes grammemes) {
      this.value = value;
      this.grammemes = grammemes;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Ending other = (Ending) obj;
      if (grammemes == null) {
        if (other.grammemes != null)
          return false;
      } else if (!grammemes.equals(other.grammemes))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result =
          prime * result + ((grammemes == null) ? 0 : grammemes.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
    }

    public Grammemes getGrammemes() {
      return grammemes;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return value + " " + grammemes;
    }

  }

  private static final long serialVersionUID = -1150852973884642646L;

  List<Ending> endings = new LinkedList<Ending>();

  int hashCode = -1;
  int modelNum;

  public void addEnding(String value, Grammemes grammemes) {
    endings.add(new Ending(value, grammemes));
  }

  /**
   * Рассчитать хэш-сумму объекта.
   * 
   * Очень важный метод -- обязательно должен вызываться после внесения изменений в объект. 
   */
  void calculateHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endings == null) ? 0 : endings.hashCode());
    this.hashCode = result;
  }

  /*
   *
   * Специально сформированная ф-я расчета хэше (без учета modelNum).
   * 
   * Основное правило: Если хеш-коды разные, то и входные объекты гарантированно
   * разные. Если хеш-коды равны, то входные объекты не всегда равны.
   */@Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EndingModel other = (EndingModel) obj;

    // используем знаниеи о хэш сумме

    if (this.hashCode != other.hashCode)
      return false;

    if (endings == null) {
      if (other.endings != null)
        return false;
    } else if (!endings.equals(other.endings))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  public List<Ending> getEndings() {
    return endings;
  }

  @Override
  public String toString() {
    return "[hashCode=" + hashCode + ", modelNum=" + modelNum + ", endings="
        + endings + "]";
  }

}
