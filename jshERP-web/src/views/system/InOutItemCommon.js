export function getProjectStatusText(projectStatus, status) {
  if (status === '1') {
    return '已完成'
  }
  switch (projectStatus) {
    case '1' :
      return '施工中'
    case '2' :
      return '资料完成中'
    case '3' :
      return '验收中'
    case '4' :
      return '送审中'
    case '5' :
      return '已开票'
    case '6' :
      return '已完成'
    case '99' :
      return '项目合并'
  }
}